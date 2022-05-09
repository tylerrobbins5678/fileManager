package us.tylerrobbins.fileManager.file;

import java.io.IOException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDAO {

  public void saveFile(FileModel file);

  public void deleteFile(FileModel file);

  public InputStreamResource loadFile(FileModel file) throws IllegalStateException, IOException;
}
