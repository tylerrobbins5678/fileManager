package us.tylerrobbins.fileManager.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Hashtable;
import org.junit.jupiter.api.Test;

// test utils package, mockito not required as all methods are static and have no dependencies
public class UtilsTest {
  // test standardizeQueryPath

  @Test
  public void whenPathIsNull_thenResultPathIsCorrect() {

    // test no value returns '/'
    String testPath = "";
    String resultPath = PathOperations.standardizeQueryPath(testPath);
    assertTrue(resultPath.equals("/"));

  }

  @Test
  public void whenPathHasNoLeading_thenResultPathIsCorrect() {

    String testPath = "test";
    String resultPath = PathOperations.standardizeQueryPath(testPath);
    assertTrue(resultPath.equals("/test"));
  }

  @Test
  public void whenPathHasFollowing_thenResultPathIsCorrect() {

    String testPath = "test/";
    String resultPath = PathOperations.standardizeQueryPath(testPath);
    assertTrue(resultPath.equals("/test"));
  }

  // test seperateDirectoryAndFile
  // assume all input is standardized beforehand
  @Test
  public void whenHasNoFiles_thenResultIsNull() {

    String testPath = "/";
    Hashtable<String, String> resultTable1 = PathOperations.seperateDirectoryAndFile(testPath);
    String fileName = resultTable1.get("name");
    String directory = resultTable1.get("directory");
    assertTrue(fileName.equals(""));
    assertTrue(directory.equals("/"));
  }

  @Test
  public void whenHasNoFolders_thenFoldersAreCorrect() {

    String testPath = "/test";
    Hashtable<String, String> resultTable2 = PathOperations.seperateDirectoryAndFile(testPath);
    String fileName = resultTable2.get("name");
    String directory = resultTable2.get("directory");
    assertTrue(fileName.equals("test"));
    assertTrue(directory.equals("/"));
  }

  @Test
  public void whenHasSeveralFolders_thenFoldersAreCorrect() {

    String testPath = "/test/test2/filedir/somedir/file.exe";
    Hashtable<String, String> resultTable2 = PathOperations.seperateDirectoryAndFile(testPath);
    String fileName = resultTable2.get("name");
    String directory = resultTable2.get("directory");
    assertTrue(fileName.equals("file.exe"));
    assertTrue(directory.equals("/test/test2/filedir/somedir"));
  }

  @Test
  public void whenNotStandardized_thenFoldersAreCorrect() {

    String testPath = "test/test2/filedir/somedir/file.exe";
    Hashtable<String, String> resultTable2 = PathOperations.seperateDirectoryAndFile(testPath);
    String fileName = resultTable2.get("name");
    String directory = resultTable2.get("directory");
    assertTrue(fileName.equals("file.exe"));
    assertTrue(directory.equals("/test/test2/filedir/somedir"));
  }
}
