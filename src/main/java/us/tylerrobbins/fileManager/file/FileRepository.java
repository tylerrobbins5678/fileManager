package us.tylerrobbins.fileManager.file;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface FileRepository extends MongoRepository<FileModel, String> {

  public List<FileModel> findByFilePath(String filePath);

  // cannot neatly return string, must return object.
  @Query(value = "{ 'filePath' : ?0 }", fields = "{ 'name' : 1 ,'_id': 0} ")
  public List<FileModel> findByFilePathIncludeNameField(String filePath);

  public boolean existsByNameAndFilePath(String name, String filePath);

  public boolean existsByFilePath(String filePath);

  public Optional<FileModel> findByFilePathAndName(String path, String name);

  public List<FileModel> findByFilePathStartingWith(String path);



}
