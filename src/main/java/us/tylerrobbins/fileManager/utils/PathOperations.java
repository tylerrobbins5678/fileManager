package us.tylerrobbins.fileManager.utils;

import java.util.Arrays;
import java.util.Hashtable;

public class PathOperations {

  public static Hashtable<String, String> seperateDirectoryAndFile(String path) {
    // use primitave types for optomization
    // assumes there is "/" at the front of the path
    String[] pathArray = path.split("/", -1);
    String[] directoryArray = Arrays.copyOfRange(pathArray, 1, pathArray.length - 1);
    String directory = "";

    for (String i : directoryArray) {
      directory += "/" + i;
    }

    String fileName = Arrays.copyOfRange(pathArray, pathArray.length - 1, pathArray.length)[0];
    Hashtable<String, String> results = new Hashtable<String, String>();
    results.put("directory", directory);
    results.put("name", fileName);

    return results;

  }
}
