package us.tylerrobbins.fileManager.user;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class UserRestServiceImpl implements UserService {

  @Autowired
  RestTemplate restTemplate;

  private String loginUrl = "http://127.0.0.1:8080/account/login";

  public Optional<UserModel> authorize(String email, String password) {
    // TODO call user api to authenicate user

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> request =
        new HttpEntity<String>("email : " + email + ", password : " + password, headers);

    UserModel user = restTemplate.postForObject(loginUrl, request, UserModel.class);

    if (user.getId() != null) {
      return Optional.of(user);
    } else {
      return Optional.empty();
    }
  }

}
