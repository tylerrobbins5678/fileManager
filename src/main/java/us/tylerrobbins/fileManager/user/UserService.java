package us.tylerrobbins.fileManager.user;

import java.util.Optional;

public interface UserService {

  public Optional<UserModel> authorize(String email, String password);

}
