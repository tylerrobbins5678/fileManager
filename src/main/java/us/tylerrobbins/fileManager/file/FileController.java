package us.tylerrobbins.fileManager.file;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import us.tylerrobbins.fileManager.user.UserModel;


@RestController
public class FileController {

  @Autowired
  FileService fileService;


  @RequestMapping(path = "/**", method = {RequestMethod.POST})
  public void handleFileUpload(HttpServletRequest request,
      @RequestParam(required = true, name = "file") MultipartFile file,
      @RequestHeader(required = true, name = "email") String email,
      @RequestHeader(required = true, name = "password") String password) {
    // get path and auth user
    String path = request.getRequestURI();
    UserModel user = fileService.authorize(email, password);
    fileService.createFile(file, path, user);
  }

  @RequestMapping(path = "/**", method = {RequestMethod.PUT})
  public void handleFileUpdate(HttpServletRequest request,
      @RequestParam(required = true, name = "file") MultipartFile file,
      @RequestHeader(required = true, name = "email") String email,
      @RequestHeader(required = true, name = "password") String password) {
    // get path and auth user
    String path = request.getRequestURI();
    UserModel user = fileService.authorize(email, password);
    fileService.updateFile(file, path, user);
  }

  @GetMapping("/**")
  public ResponseEntity<Resource> fileDownload(HttpServletRequest request,
      @RequestHeader(required = true, name = "email") String email,
      @RequestHeader(required = true, name = "password") String password) {

    String path = request.getRequestURI();
    UserModel user = fileService.authorize(email, password);
    InputStreamResource file = fileService.getFile(path, user);

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + file.getFilename() + "\"").body(file);
  }

  @RequestMapping(path = "/**", method = {RequestMethod.DELETE})
  public void fileDelete(HttpServletRequest request,
      @RequestHeader(required = true, name = "email") String email,
      @RequestHeader(required = true, name = "password") String password) {

    String path = request.getRequestURI();
    UserModel user = fileService.authorize(email, password);
    fileService.deleteFile(path, user);

  }

}
