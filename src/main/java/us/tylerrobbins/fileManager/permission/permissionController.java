package us.tylerrobbins.fileManager.permission;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tylerrobbins.fileManager.user.UserModel;


@RestController
@RequestMapping("${fileManager.permsiion-prefix}" + "/" + "${fileManager.defaultPath}")
public class permissionController {

  @Value("${fileManager.permsiion-prefix}")
  String permissionPrefix;

  @Autowired
  PermissionServiceImpl permissionService;

  // get full permissions list on file
  @RequestMapping(path = "/**", method = {RequestMethod.GET})
  public HashMap<String, PermissionModel> getPermissionsList(HttpServletRequest request,
      @RequestHeader(required = true, name = "email") String email,
      @RequestHeader(required = true, name = "password") String password) {

    // auth user
    UserModel user = permissionService.authorize(email, password);
    // get path and strip "/permission" prefix
    String path = request.getRequestURI();
    path = path.replaceFirst("/" + permissionPrefix, "");


    return permissionService.getPermissions(path, user);
  }

  @RequestMapping(path = "/**", method = {RequestMethod.POST})
  public PermissionSucessErrors addPermissionsPost(HttpServletRequest request,
      @RequestHeader(required = true, name = "email") String email,
      @RequestHeader(required = true, name = "password") String password,
      @RequestBody HashMap<String, PermissionModel> permissions) {

    // auth user
    UserModel user = permissionService.authorize(email, password);
    // get path and strip "/permission" prefix
    String path = request.getRequestURI();
    path = path.replaceFirst("/" + permissionPrefix, "");


    return permissionService.addPermissions(user, permissions, path, false);
  }

  @RequestMapping(path = "/**", method = {RequestMethod.PUT})
  public PermissionSucessErrors updatePermissionPut(HttpServletRequest request,
      @RequestHeader(required = true, name = "email") String email,
      @RequestHeader(required = true, name = "password") String password,
      @RequestBody HashMap<String, PermissionModel> permissions) {

    // auth user
    UserModel user = permissionService.authorize(email, password);
    // get path and strip "/permission" prefix
    String path = request.getRequestURI();
    path = path.replaceFirst("/" + permissionPrefix, "");


    return permissionService.addPermissions(user, permissions, path, true);
  }

  @RequestMapping(path = "/**", method = {RequestMethod.DELETE})
  public PermissionSucessErrors deletePermissionsDelete(HttpServletRequest request,
      @RequestHeader(required = true, name = "email") String email,
      @RequestHeader(required = true, name = "password") String password,
      @RequestBody List<String> permissions) {

    // auth user
    UserModel user = permissionService.authorize(email, password);
    // get path and strip "/permission" prefix
    String path = request.getRequestURI();
    path = path.replaceFirst("/" + permissionPrefix, "");


    return permissionService.deletePermissions(user, permissions, path);
  }
}
