package us.tylerrobbins.fileManager.file;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

  public void saveFile(MultipartFile file, String email, String password);

  public InputStreamResource getFile(String fileName, String email, String password);

}
