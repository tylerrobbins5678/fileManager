package us.tylerrobbins.fileManager.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class FileController {

  @Autowired
  FileService fileService;


  @PostMapping("/")
  public void handleFileUpload(@RequestParam("file") MultipartFile file,
      @RequestHeader("email") String email, @RequestHeader("password") String password) {

    fileService.saveFile(file, email, password);
  }

  @GetMapping("/{fileName}")
  public ResponseEntity<Resource> fileDownload(String fileName,
      @RequestHeader("email") String email, @RequestHeader("password") String password) {

    InputStreamResource file = fileService.getFile(fileName, email, password);

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + file.getFilename() + "\"").body(file);
  }

}
