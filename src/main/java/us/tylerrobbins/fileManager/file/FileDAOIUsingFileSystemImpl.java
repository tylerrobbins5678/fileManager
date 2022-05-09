package us.tylerrobbins.fileManager.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class FileDAOIUsingFileSystemImpl implements FileDAO {

  public void saveFile(FileModel file) {

    try {
      File directory = new File("${fileManager.defaultPath}" + file.getFilePath());
      File fileobj = new File(directory.getPath(), file.getName());

      if (!directory.exists()) {
        directory.mkdirs();
      }

      if (fileobj.exists()) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "file already exist");
      }

      file.getFileObj().transferTo(fileobj);

    } catch (IllegalStateException | IOException e) {
      System.out.println(e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "unable to save file");
    }
  }

  public InputStreamResource loadFile(FileModel file) throws FileNotFoundException {

    return new InputStreamResource(
        new FileInputStream(new File("${fileManager.defaultPath}" + file.getFilePath())));

  }

  @Override
  public void deleteFile(FileModel file) {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateFile(FileModel file) {
    // TODO Auto-generated method stub

  }

}
