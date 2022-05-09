package us.tylerrobbins.fileManager.file;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;

@Service
@Primary
public class FileDAOUsingGridFSImpl implements FileDAO {

  @Autowired
  GridFsTemplate gridFs;

  @Autowired
  FileRepository fileRepository;

  public void saveFile(FileModel file) {
    // create file in gridfs

    MultipartFile fileOBJ = file.getFileObj();
    DBObject metadata = new BasicDBObject();
    metadata.put("fileSize", fileOBJ.getSize());

    String fileId = null;
    try {
      fileId = gridFs.store(fileOBJ.getInputStream(), fileOBJ.getOriginalFilename(),
          fileOBJ.getContentType(), metadata).toString();

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // set file id of fileobj
    file.setFileId(fileId);
  }

  public void deleteFile(FileModel file) {
    gridFs.delete(new Query(Criteria.where("_id").is(file.getFileId())));
  }

  public InputStreamResource loadFile(FileModel file) throws IllegalStateException, IOException {

    String fileId = file.getFileId();
    GridFSFile gridFSFile = gridFs.findOne(new Query(Criteria.where("_id").is(fileId)));

    return new InputStreamResource(gridFs.getResource(gridFSFile).getInputStream());

  }

  public void updateFile(FileModel file) {
    // save file refrence & insert new fileObj
    String fileId = file.getFileId();
    saveFile(file);

    // update fileObj refrence
    fileRepository.save(file);

    // replace fileId with non-refrenced fileObj and delete
    file.setFileId(fileId);
    deleteFile(file);
  }

}
