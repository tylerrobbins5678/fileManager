package us.tylerrobbins.fileManager.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import us.tylerrobbins.fileManager.permission.PermissionModel;
import us.tylerrobbins.fileManager.user.UserModel;
import us.tylerrobbins.fileManager.user.UserService;
import us.tylerrobbins.fileManager.utils.PathOperations;


@Service
public class FileServiceImpl implements FileService {

  @Autowired
  UserService userService;

  @Autowired
  FileDAO fileIO;

  @Autowired
  FileRepository fileRepository;


  // auth user or return 401
  public UserModel authorize(String email, String password) {
    Optional<UserModel> user = userService.authorize(email, password);

    if (!user.isPresent()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials");
    } else {
      return user.get();
    }
  }

  // returns file model or throws 404 if not found
  public FileModel getFileModelFromDb(String path) {
    // strip path to folder directory and filename
    path = PathOperations.standardizeQueryPath(path);
    Hashtable<String, String> rs = PathOperations.seperateDirectoryAndFile(path);
    path = rs.get("directory");
    String name = rs.get("name");
    // get file from db
    Optional<FileModel> fileOptional = fileRepository.findByFilePathAndName(path, name);

    FileModel file = null;
    if (!fileOptional.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, path + name + " not found");
    } else {
      return fileOptional.get();
    }
  }

  public Hashtable<String, List<String>> getSubFolders(String path) {
    path = PathOperations.standardizeQueryPath(path);

    // generate list of complete paths from
    List<String> subFolders = fileRepository.findByFilePathStartingWith(path + "/").stream()
        .map(x -> x.getFilePath()).collect(Collectors.toList());

    // get set of subfolders from complete path
    HashSet<String> subFoldersSet = PathOperations.getSubFoldersFromFolder(path, subFolders);
    List<String> subFoldersList = new ArrayList<String>(subFoldersSet);

    // get list of fileModels, get list of names from filemodels
    List<String> fileNames = fileRepository.findByFilePathIncludeNameField(path).stream()
        .map(x -> x.getName()).collect(Collectors.toList());

    // create hashtable and add elements
    Hashtable<String, List<String>> filesAndFolders = new Hashtable<String, List<String>>();
    filesAndFolders.put("files", fileNames);
    filesAndFolders.put("folders", subFoldersList);

    return filesAndFolders;
  }

  // creates the file, thows 409 if file already exist
  public void createFile(MultipartFile fileObj, String path, UserModel user) {
    path = PathOperations.standardizeQueryPath(path);
    // create filemodel to save
    FileModel file = new FileModel();
    file.setName(fileObj.getOriginalFilename());
    file.setFilePath(path);

    // check if file already exist via repository
    if (fileRepository.existsByNameAndFilePath(file.getName(), file.getFilePath())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "file at path already exist");
    }
    // check if foler already exist with same name as file
    if (fileRepository.existsByFilePath(file.getFilePath())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "folder at path already exist");
    }

    // create fileOBJ after all possible exceptions
    file.setFileObj(fileObj);

    // add self to permissions
    PermissionModel selfPermissions = new PermissionModel();
    selfPermissions.grantAllPermissions();

    // add default permissions
    PermissionModel defaultPermissions = new PermissionModel();
    defaultPermissions.grantNoPermissions();


    // add permissions to fileobj
    file.addPermissions(user.getId(), selfPermissions);
    file.addPermissions(0, defaultPermissions);

    fileIO.saveFile(file);

    fileRepository.save(file);

    // save to disk omitted due to gridfs
    // fileIO.saveFile(fileObj);


  }

  // retreives the file has acecss
  public InputStreamResource getFile(String path, UserModel user) {

    FileModel file = getFileModelFromDb(path);

    // check if user exist in permissions
    Boolean canRead = false;
    if (file.getPermissions().containsKey(user.getId())) {
      canRead = file.getPermissions().get(user.getId()).getCanRead();
    } else {
      // use user id 0 as default
      canRead = file.getPermissions().get(0).getCanRead();
    }

    if (canRead) {
      try {
        return fileIO.loadFile(file);
      } catch (IllegalStateException | IOException e) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
            "file information found, file not found");
      }

    } else {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "restricted access");
    }
  }

  // cannot replace file directly, must delete and reinsert
  public void updateFile(MultipartFile fileObj, String path, UserModel user) {

    FileModel file = getFileModelFromDb(path);
    file.setFileObj(fileObj);

    // chack user permissions on path / file
    Boolean canUpdate = file.getPermissions().get(user.getId()).getCanUpdate();

    if (canUpdate) {
      // save file refrence & insert new fileObj
      String fileId = file.getFileId();
      fileIO.saveFile(file);

      // update fileObj refrence
      fileRepository.save(file);

      // replace with non-refrenced fileObj
      file.setFileId(fileId);
      fileIO.deleteFile(file);

    } else {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "restricted access");
    }

  }

  public void deleteFile(String path, UserModel user) {

    FileModel file = getFileModelFromDb(path);
    Boolean canDelete = file.getPermissions().get(user.getId()).getCanDelete();

    if (canDelete) {
      // delete refrence first
      fileRepository.delete(file);
      fileIO.deleteFile(file);

    } else {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "restricted access");
    }
  }

}
