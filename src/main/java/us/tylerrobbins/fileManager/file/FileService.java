package us.tylerrobbins.fileManager.file;

import java.util.Hashtable;
import java.util.List;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import us.tylerrobbins.fileManager.user.UserModel;

public interface FileService {

  public Hashtable<String, List<String>> getSubFolders(String path);

  public UserModel authorize(String email, String password);

  public void createFile(MultipartFile file, String path, UserModel user);

  public InputStreamResource getFile(String path, UserModel user);

  public void updateFile(MultipartFile file, String path, UserModel user);

  public void deleteFile(String path, UserModel user);

}
