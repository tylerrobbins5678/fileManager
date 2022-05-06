package us.tylerrobbins.fileManager.file;

import java.util.HashMap;
import org.springframework.data.annotation.Transient;
import org.springframework.web.multipart.MultipartFile;
import us.tylerrobbins.fileManager.permission.PermissionModel;

public class FileModel {

  String name;
  String description;
  String filePath;
  // this is a hex value, but a string will work
  String id;
  String fileId;
  String owner;

  // must be ignored and stored using file system or GridFs
  @Transient
  MultipartFile fileObj;

  HashMap<Integer, PermissionModel> permissions = new HashMap<Integer, PermissionModel>();


  public MultipartFile getFileObj() {
    return fileObj;
  }

  public void setFileObj(MultipartFile fileObj) {
    this.fileObj = fileObj;
  }

  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String path) {
    this.filePath = path;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public HashMap<Integer, PermissionModel> getPermissions() {
    return permissions;
  }

  public void setPermissions(HashMap<Integer, PermissionModel> permissions) {
    this.permissions = permissions;
  }

  public void addPermissions(Integer Integer, PermissionModel permissions) {
    this.permissions.put(Integer, permissions);
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

}
