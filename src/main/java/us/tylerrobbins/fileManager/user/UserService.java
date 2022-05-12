package us.tylerrobbins.fileManager.user;

import java.util.Optional;
import java.util.concurrent.Future;

public interface UserService {

  public Optional<UserModel> authorize(String email, String password);

  public Future<UserModel> getUserByIdAsync(Integer id);

  public Future<UserModel> getUserByEmailAsync(String email);

}
