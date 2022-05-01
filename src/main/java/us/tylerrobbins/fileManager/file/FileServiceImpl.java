package us.tylerrobbins.fileManager.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import us.tylerrobbins.fileManager.user.UserModel;
import us.tylerrobbins.fileManager.user.UserService;

public class FileServiceImpl implements FileService {

  @Autowired
  UserService userService;

  public void saveFile(MultipartFile file, String email, String password) {

    // auth user before get file
    Optional<UserModel> user = userService.authorize(email, password);

    if (!user.isPresent()) {
      // TODO return 403 response
    } else {

    }

  }

  @Override
  public InputStreamResource getFile(String fileName, String email, String password) {

    // auth user before get file
    Optional<UserModel> user = userService.authorize(email, password);

    if (!user.isPresent()) {
      // TODO return 403 response
      return null;
    } else {
      try {
        return new InputStreamResource(new FileInputStream(fileName));
      } catch (FileNotFoundException e) {
        // TODO return 404 response
        e.printStackTrace();
        return null;

      }

    }

  }


}
