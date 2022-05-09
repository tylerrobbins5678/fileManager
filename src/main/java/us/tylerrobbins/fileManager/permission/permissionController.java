package us.tylerrobbins.fileManager.permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tylerrobbins.fileManager.file.FileService;


@RestController
@RequestMapping("permission")
public class permissionController {

  @Autowired
  FileService fileService;

  @RequestMapping(path = "/", method = {RequestMethod.GET})
  public String getSubFolders() {
    // auth user
    return "it works";
  }
}
