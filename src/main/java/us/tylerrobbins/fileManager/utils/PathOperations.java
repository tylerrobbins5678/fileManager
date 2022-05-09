package us.tylerrobbins.fileManager.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public class PathOperations {

  public static Hashtable<String, String> seperateDirectoryAndFile(String path) {

    // standardize path in
    path = standardizeQueryPath(path);
    String[] pathArray = path.split("/", -1);
    String[] directoryArray = Arrays.copyOfRange(pathArray, 1, pathArray.length - 1);
    String directory = "";

    for (String i : directoryArray) {
      directory += "/" + i;
    }

    String fileName = Arrays.copyOfRange(pathArray, pathArray.length - 1, pathArray.length)[0];
    Hashtable<String, String> results = new Hashtable<String, String>();
    // standardize path out
    results.put("directory", standardizeQueryPath(directory));
    results.put("name", fileName);

    return results;

  }

  public static HashSet<String> getSubFoldersFromFolder(String folder, List<String> subFolder) {

    // get number of subfolders, index 0 will always be before leading '/', must be >= 1
    int index = Math.max(folder.split("/").length, 1);


    for (int i = 0; i < subFolder.size(); i++) {
      // use .split and grab n index from split strings
      subFolder.set(i, subFolder.get(i).split("/")[index]);
    }

    return new HashSet<String>(subFolder);
  }

  public static String standardizeQueryPath(String path) {
    // force not null path
    if (path.equals("")) {
      path = "/";
    }
    // add leading "/" if not present
    // this is prevents /dir/dir2 and dir/dir2 from being shown as seperate directories

    if (path.charAt(0) != '/') {
      path = "/" + path;
    }

    // remove trailing '/' if present, add exception for '/'
    if (path.charAt(path.length() - 1) == '/' && !path.equals("/")) {
      path = path.substring(0, path.length() - 1);
    }

    return path;
  }
}
