package us.tylerrobbins.fileManager.user;

import java.util.Optional;
import java.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Service
public class UserRestServiceImpl implements UserService {

  @Autowired
  RestTemplate restTemplate;

  private String baseUrl = "http://127.0.0.1:8080/account";

  public Optional<UserModel> authorize(String email, String password) {
    // call user api to authenicate user

    String requestJson =
        "{\"email\": \"" + email + "\" , \"password\": " + "\"" + password + "\" }";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> request = new HttpEntity<String>(requestJson, headers);

    UserModel user = restTemplate.postForObject(baseUrl + "/login", request, UserModel.class);

    if (user != null) {
      return Optional.of(user);
    } else {
      return Optional.empty();
    }
  }

  @Async
  public Future<UserModel> getUserByIdAsync(Integer id) throws HttpClientErrorException {

    try {
      return new AsyncResult<UserModel>(
          restTemplate.getForObject(baseUrl + "/id/" + "{id}", UserModel.class, id));
    } catch (HttpClientErrorException e) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
    }
  }

  @Async
  public Future<UserModel> getUserByEmailAsync(String email) throws HttpClientErrorException {
    try {
      return new AsyncResult<UserModel>(
          restTemplate.getForObject(baseUrl + "/email/" + "{email}", UserModel.class, email));
    } catch (HttpClientErrorException e) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
    }
  }



}
