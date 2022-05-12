package us.tylerrobbins.fileManager.permission;

import java.util.Optional;
import org.junit.After;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import us.tylerrobbins.fileManager.file.FileModel;
import us.tylerrobbins.fileManager.file.FileRepository;
import us.tylerrobbins.fileManager.user.UserModel;
import us.tylerrobbins.fileManager.user.UserService;

// test permission service
@ExtendWith(MockitoExtension.class)
public class PermissionTest {


  @Mock
  UserService userServiceMock;
  @Mock
  FileRepository fileRepositoryMock;

  private UserModel realUser;
  private FileModel realFile;
  private PermissionModel accessAll;
  private PermissionModel accessNone;

  @InjectMocks
  PermissionServiceImpl permissionServiceMock;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);

    // fake user
    realUser = new UserModel();
    realUser.setEmail("RealEmail@com.com");
    realUser.setFirstName("RealNmae");
    realUser.setId(50);
    realUser.setLastName("RealLastName");

    // fake permissions
    PermissionModel accessAll = new PermissionModel();
    accessAll.grantAllPermissions();

    PermissionModel accessNone = new PermissionModel();
    accessNone.grantNoPermissions();

    // fake file
    realFile = new FileModel();
    realFile.setDescription(" its just a file");
    realFile.setFileId("this is a 32 char field");
    realFile.setFileObj(new MockMultipartFile("foo", "../foo.txt", MediaType.TEXT_PLAIN_VALUE,
        "Hello World".getBytes()));
    realFile.setFilePath("/path/to/file");
    realFile.setName("foo.txt");
    realFile.setOwner("Owner");

  }

  @After
  public void cleanup() {

  }

  // PermissionService authorize
  @Test()
  public void whenLoginIsIncorrect_thenUnothorizedIsReturned() {
    Mockito.when(userServiceMock.authorize("notARealEmail@userServiceMock.com", "fakePassword"))
        .thenReturn(Optional.empty());
    Assertions.assertThrows(ResponseStatusException.class, () -> {
      permissionServiceMock.authorize("notARealEmail@userServiceMock.com", "fakePassword");
    });
  }

  // PermissionService authorize
  @Test()
  public void whenLoginIsCorrect_thenUserIsReturned() {
    Mockito.when(userServiceMock.authorize("notARealEmail@userServiceMock.com", "realPassword"))
        .thenReturn(Optional.of(realUser));
    Assertions.assertEquals(
        permissionServiceMock.authorize("notARealEmail@userServiceMock.com", "realPassword"),
        realUser);
  }


}
