package com.kob.backend.service.user.account;

import org.springframework.web.multipart.MultipartFile;




public interface PhotoService {
    public String uploadImage(MultipartFile file);
}
