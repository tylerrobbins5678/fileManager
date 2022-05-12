package us.tylerrobbins.fileManager.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${fileManager.defaultPath}")
  String defaultPath;

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
  public Optional<FileModel> getFileModelFromDb(String path) {
    // strip path to folder directory and filename
    path = PathOperations.standardizeQueryPath(path);
    Hashtable<String, String> rs = PathOperations.seperateDirectoryAndFile(path);
    path = rs.get("directory");
    String name = rs.get("name");
    // get file from db
    Optional<FileModel> fileOptional = fileRepository.findByFilePathAndName(path, name);

    return fileOptional;
  }

  public Hashtable<String, List<String>> getSubFolders(String path) {
    // add default path as prefix to query path must sterilize before and after
    path = PathOperations.standardizeQueryPath(path);
    path = "/" + defaultPath + path;
    path = PathOperations.standardizeQueryPath(path);

    // generate list of complete paths from subfolder
    // always add "/" to enforce path contains sub directory
    List<String> subFolders = fileRepository.findByFilePathStartingWith(path + "/").stream()
        .map(x -> x.getFilePath()).collect(Collectors.toList());

    // get set of subfolders from complete paths
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

    Optional<FileModel> fileOpt = getFileModelFromDb(path);

    FileModel file = null;
    if (!fileOpt.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, path + " not found");
    } else {
      file = fileOpt.get();
    }

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

    Optional<FileModel> fileOpt = getFileModelFromDb(path);

    FileModel file = null;
    if (!fileOpt.isPresent()) {
      // create if not present and return
      createFile(fileObj, path, user);
      return;
    } else {
      file = fileOpt.get();
    }
    file.setFileObj(fileObj);

    Boolean canUpdate = false;
    if (file.getPermissions().containsKey(user.getId())) {
      canUpdate = file.getPermissions().get(user.getId()).getCanUpdate();

    } else {
      // use 0 as key for default access
      canUpdate = file.getPermissions().get(0).getCanUpdate();
    }

    // chack user permissions on path / file
    // throw 403 if key not in permissions

    if (canUpdate) {
      fileIO.updateFile(file);

    } else {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "restricted access");
    }

  }

  public void deleteFile(String path, UserModel user) {

    Optional<FileModel> fileOpt = getFileModelFromDb(path);
    FileModel file = null;
    if (!fileOpt.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, path + " not found");
    } else {
      file = fileOpt.get();
    }

    // access control for can delete
    Boolean canDelete = false;
    if (file.getPermissions().containsKey(user.getId())) {
      canDelete = file.getPermissions().get(user.getId()).getCanDelete();

    } else {
      // use 0 as key for default access
      canDelete = file.getPermissions().get(0).getCanDelete();
    }

    if (canDelete) {
      // delete refrence first
      fileRepository.delete(file);
      fileIO.deleteFile(file);

    } else {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "restricted access");
    }
  }

}
