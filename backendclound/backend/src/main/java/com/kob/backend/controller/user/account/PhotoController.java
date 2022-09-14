package com.kob.backend.controller.user.account;

import com.kob.backend.service.user.account.PhotoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
public class PhotoController {
    @Autowired
    private PhotoService photoService;

    @PostMapping("/api/user/account/upload_photo/")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file){

        String url= photoService.uploadImage(file);
        log.info("返回地址：【{}】",url);
        return ResponseEntity.ok(url);
    }

}
