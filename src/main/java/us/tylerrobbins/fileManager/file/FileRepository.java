package us.tylerrobbins.fileManager.file;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileRepository extends MongoRepository<FileModel, String> {

  public List<FileModel> findByFilePath(String filePath);

  public boolean existsByNameAndFilePath(String name, String filePath);

  public Optional<FileModel> findByFilePathAndName(String path, String name);


}
