package us.tylerrobbins.fileManager.file;

import java.util.HashMap;

public class FileModel {

  String name;
  String description;
  // this is a hex value, but a string will work
  String id;

  HashMap<String, Permission> permissions = new HashMap<String, Permission>();
}
