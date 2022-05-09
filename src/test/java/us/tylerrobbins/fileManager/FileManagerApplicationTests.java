package us.tylerrobbins.fileManager;

import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;
import us.tylerrobbins.fileManager.file.FileService;
import us.tylerrobbins.fileManager.user.UserModel;
import us.tylerrobbins.fileManager.user.UserService;

@RunWith(SpringRunner.class)
class FileManagerApplicationTests {

  @InjectMocks
  private FileService fileService;

  @InjectMocks
  private UserService userService;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void whenUsernameAndPasswordIsProvided_thenRetrievedIdIsCorrect() {


    UserModel user = new UserModel();
    user.setEmail("tylerrobbins5678@gmail.com");
    user.setFirstName("tyler");
    user.setId(3);
    user.setLastName("lname");

    // mock call to UserService returning optional
    Mockito.when(userService.authorize("tylerrobbins5678@gmail.com", "NotReal"))
        .thenReturn(Optional.of(user));

    Mockito.when(user.getId()).thenReturn(3);

    // test call to fileservice to get object
    int id = fileService.authorize("tylerrobbins5678@gmail.com", "NotReal").getId();
    Assert.assertEquals(id, 3);

  }

}
