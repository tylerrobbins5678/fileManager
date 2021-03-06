package us.tylerrobbins.fileManager.permission;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class PermissionModel {

  Boolean canRead;
  Boolean canUpdate;
  Boolean canDelete;
  Boolean canAdmin;

  public void grantAllPermissions() {
    this.canRead = true;
    this.canUpdate = true;
    this.canDelete = true;
    this.canAdmin = true;
  }

  public void grantNoPermissions() {
    this.canRead = false;
    this.canUpdate = false;
    this.canDelete = false;
    this.canAdmin = false;
  }

  public Boolean getCanAdmin() {
    return canAdmin;
  }


  public void setCanAdmin(Boolean canAdmin) {
    this.canAdmin = canAdmin;
  }


  public Boolean getCanRead() {
    return canRead;
  }

  public void setCanRead(Boolean canRead) {
    this.canRead = canRead;
  }

  public Boolean getCanUpdate() {
    return canUpdate;
  }

  public void setCanUpdate(Boolean canUpdate) {
    this.canUpdate = canUpdate;
  }

  public Boolean getCanDelete() {
    return canDelete;
  }

  public void setCanDelete(Boolean canDelete) {
    this.canDelete = canDelete;
  }


}
