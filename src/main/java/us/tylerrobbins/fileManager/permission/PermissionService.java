package us.tylerrobbins.fileManager.permission;

import java.util.HashMap;
import java.util.List;
import us.tylerrobbins.fileManager.user.UserModel;

public interface PermissionService {

  public UserModel authorize(String email, String password);

  public HashMap<String, PermissionModel> getPermissions(String path, UserModel user);

  public PermissionSucessErrors addPermissions(UserModel user,
      HashMap<String, PermissionModel> permissions, String path, Boolean overwrite);

  public PermissionSucessErrors deletePermissions(UserModel user, List<String> permissions,
      String path);


}
