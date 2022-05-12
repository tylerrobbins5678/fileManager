package us.tylerrobbins.fileManager.permission;

import java.util.HashMap;
import java.util.List;

// simple return object for request status
public class PermissionSucessErrors {
  private List<String> success;
  // email, reason for error
  private HashMap<String, String> errors;

  public List<String> getSuccess() {
    return success;
  }

  public void setSuccess(List<String> success) {
    this.success = success;
  }

  public HashMap<String, String> getErrors() {
    return errors;
  }

  public void setErrors(HashMap<String, String> errors) {
    this.errors = errors;
  }

}
