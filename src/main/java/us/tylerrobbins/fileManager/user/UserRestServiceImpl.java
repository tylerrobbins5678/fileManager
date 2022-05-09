package us.tylerrobbins.fileManager.user;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class UserRestServiceImpl implements UserService {

  @Autowired
  RestTemplate restTemplate;

  private String loginUrl = "http://127.0.0.1:8080/account/login";

  public Optional<UserModel> authorize(String email, String password) {
    // call user api to authenicate user

    String requestJson =
        "{\"email\": \"" + email + "\" , \"password\": " + "\"" + password + "\" }";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> request = new HttpEntity<String>(requestJson, headers);

    UserModel user = restTemplate.postForObject(loginUrl, request, UserModel.class);

    if (user != null) {
      return Optional.of(user);
    } else {
      return Optional.empty();
    }
  }

}
