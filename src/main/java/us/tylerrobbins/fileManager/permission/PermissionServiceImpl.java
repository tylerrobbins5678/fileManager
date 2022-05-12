package us.tylerrobbins.fileManager.permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import us.tylerrobbins.fileManager.file.FileModel;
import us.tylerrobbins.fileManager.file.FileRepository;
import us.tylerrobbins.fileManager.user.UserModel;
import us.tylerrobbins.fileManager.user.UserService;
import us.tylerrobbins.fileManager.utils.PathOperations;

@Service
public class PermissionServiceImpl implements PermissionService {

  @Autowired
  UserService userService;

  @Autowired
  FileRepository fileRepository;

  // contructor for testing with mockito
  public PermissionServiceImpl(UserService userService, FileRepository fileRepository) {
    this.fileRepository = fileRepository;
    this.userService = userService;
  }

  // auth user or return 401
  public UserModel authorize(String email, String password) {
    Optional<UserModel> user = userService.authorize(email, password);

    if (!user.isPresent()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials");
    } else {
      return user.get();
    }
  }

  // throws exception or completes
  private void checkAdmin(UserModel user, FileModel file) {
    boolean canAdmin = false;
    if (file.getPermissions().containsKey(user.getId())) {
      canAdmin = file.getPermissions().get(user.getId()).getCanAdmin();
    }
    // check if user can admin, throw 403 if not
    if (!canAdmin) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          user.getEmail() + " does not have access");
    }
  }

  private FileModel getFileInfoFromDb(String path) {
    // standardize path
    path = PathOperations.standardizeQueryPath(path);
    // seperate name and directory
    Hashtable<String, String> rs = PathOperations.seperateDirectoryAndFile(path);
    path = rs.get("directory");
    String name = rs.get("name");
    // get file from db
    Optional<FileModel> fileOptional = fileRepository.findByFilePathAndName(path, name);

    // throw 404 if there is no file
    if (!fileOptional.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "path: " + path + " file: " + name + " not found");
    } else {
      // return fileModel
      return fileOptional.get();
    }
  }


  // return all users with permissions on file if user has auth to file
  public HashMap<String, PermissionModel> getPermissions(String path, UserModel user) {

    // standardize path
    path = PathOperations.standardizeQueryPath(path);

    FileModel file = getFileInfoFromDb(path);
    // check for admin permissions

    checkAdmin(user, file);
    // get all users with any privlage on file, no built in bulk access methods
    // will use async get requests as there may be several users
    List<Future<UserModel>> userFutureList = new ArrayList<Future<UserModel>>();
    for (int id : file.getPermissions().keySet()) {
      userFutureList.add(userService.getUserByIdAsync(id));
    }

    int received = 0;
    HashMap<String, PermissionModel> emailAndPermissions = new HashMap<String, PermissionModel>();
    while (received < userFutureList.size()) {
      try {
        // get email and user permissions in file via id from userReceived
        UserModel userReceived = userFutureList.get(received).get();
        received++;
        emailAndPermissions.put(userReceived.getEmail(),
            file.getPermissions().get(userReceived.getId()));


      } catch (Exception e) {
        // received, but still an error
        received++;
        // TODO log error as network error
      }
    }
    return emailAndPermissions;
  }

  // adds permissions or overwrites them determined by overwrite field
  public PermissionSucessErrors addPermissions(UserModel user,
      HashMap<String, PermissionModel> permissions, String path, Boolean overwrite) {

    // standardize path
    path = PathOperations.standardizeQueryPath(path);
    // get file from db
    FileModel file = getFileInfoFromDb(path);
    // auth user for admin
    checkAdmin(user, file);

    // create future Hashtable of users email as key, future as value
    HashMap<String, Future<UserModel>> users = new HashMap<String, Future<UserModel>>();
    for (String userEmail : permissions.keySet()) {
      // get userModel for provided userEmail from microservice
      users.put(userEmail, userService.getUserByEmailAsync(userEmail));
    }

    List<String> success = new ArrayList<String>();
    HashMap<String, String> errors = new HashMap<String, String>();
    // retreive future and process user
    for (String userFutureEmail : users.keySet()) {
      UserModel userToAdd = new UserModel();

      try {
        userToAdd = users.get(userFutureEmail).get();
      } catch (InterruptedException e) {
        // generic error continue
        errors.put(userFutureEmail, "internal error, please try again");
        continue;

      } catch (ExecutionException e) {
        if (e.getCause() instanceof HttpClientErrorException) {
          // client 4xxx response continue
          errors.put(userFutureEmail, "user does not exist");
          continue;

        } else {
          // client 5xxx response continue
          errors.put(userFutureEmail, "internal error, please try again");
          continue;
        }
      }
      // replace if overwrite
      // replace if not present and no overwrite
      // dont replace if exist and no everwrite
      // (not exist and not overwrite) or (overwrite)
      if ((!file.getPermissions().containsKey(userToAdd.getId()) && !overwrite) || overwrite) {
        // add userid from userToAdd to file with permissions from email
        file.addPermissions(userToAdd.getId(), permissions.get(userToAdd.getEmail()));
        success.add(userToAdd.getEmail());
      } else {
        // enter error and continue
        errors.put(userFutureEmail, " already has permissions");
        continue;
      }

    }
    fileRepository.save(file);

    PermissionSucessErrors returnTemplate = new PermissionSucessErrors();
    returnTemplate.setSuccess(success);
    returnTemplate.setErrors(errors);

    return returnTemplate;
  }


  // get all users in list by email and delete id from permissions
  public PermissionSucessErrors deletePermissions(UserModel user, List<String> permissions,
      String path) {
    // standardize path
    path = PathOperations.standardizeQueryPath(path);
    // get file from db
    FileModel file = getFileInfoFromDb(path);
    // auth user for admin handles 403 if not
    checkAdmin(user, file);

    // get each userModel by email via Async call to userservice
    HashMap<String, Future<UserModel>> users = new HashMap<String, Future<UserModel>>();
    for (String userEmail : permissions) {
      // get userModel for provided userEmail from microservice
      users.put(userEmail, userService.getUserByEmailAsync(userEmail));
    }

    HashMap<Integer, PermissionModel> permissionTmp = file.getPermissions();
    List<String> success = new ArrayList<String>();
    HashMap<String, String> errors = new HashMap<String, String>();
    // process delete request and record errors / success
    for (String userFutureEmail : users.keySet()) {
      UserModel userToDelete = new UserModel();

      try {
        userToDelete = users.get(userFutureEmail).get();
      } catch (InterruptedException e) {
        // TODO log error
        errors.put(userFutureEmail, "internal error, please try again");
        continue;
      } catch (ExecutionException e) {
        if (e.getCause() instanceof HttpClientErrorException) {
          errors.put(userFutureEmail, "user does not exist");
          continue;
        } else {
          errors.put(userFutureEmail, "internal error, please try again");
          continue;
        }
      }

      // check if user exist in permissions
      // delete from fileModelTmp and save
      if (permissionTmp.containsKey(userToDelete.getId())) {
        permissionTmp.remove(userToDelete.getId());
        success.add(userToDelete.getEmail());
      } else {
        errors.put(userToDelete.getEmail(), "user not found in permissions");
        continue;
      }


    }
    file.setPermissions(permissionTmp);
    fileRepository.save(file);

    PermissionSucessErrors returnTemplate = new PermissionSucessErrors();
    returnTemplate.setSuccess(success);
    returnTemplate.setErrors(errors);

    return returnTemplate;
  }

}
