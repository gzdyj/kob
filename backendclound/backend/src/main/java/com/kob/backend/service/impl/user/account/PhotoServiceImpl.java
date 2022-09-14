package com.kob.backend.service.impl.user.account;

import com.kob.backend.config.properties.COSProperties;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utlis.UserDetailsImpl;
import com.kob.backend.service.user.account.PhotoService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.*;

@Service
@Slf4j
public class PhotoServiceImpl implements PhotoService {
    String key;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private COSClient cosClient;
    @Autowired
    private COSProperties cosProperties;
    // 支持的文件类型
    private static final List<String> suffixes = Arrays.asList("image/png", "image/jpeg");

    @Override
    public String uploadImage(MultipartFile file) {

        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();


        try {
            // 1、图片信息校验
            // 1)校验文件类型
            String type = file.getContentType(); //获取文件格式
            if (!suffixes.contains(type)) {
                return "上传失败，文件类型不匹配:"+type;
            }
            // 2)校验图片内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                return "上传失败，文件内容不符合要求";
            }

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(type);
            UUID uuid = UUID.randomUUID();
            // 指定要上传到 COS 上对象键 此key是文件唯一标识
            key = uuid.toString().replace("-","")+".jpg";
            PutObjectRequest putObjectRequest = new PutObjectRequest(cosProperties.getBucketName(), key, file.getInputStream(),objectMetadata);

            //使用cosClient调用第三方接口
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            log.info(putObjectRequest+"");
            //返回路径

        }catch (Exception e){
            e.printStackTrace();
        }
        //拼接返回路径
        String imagePath = "https://cos.zinzin.cc/"+ key + "?imageMogr2/format/webp";
        //把个人的照片更新
        User new_user = new User(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                imagePath,
                user.getRating(),
                user.getOpenid()
        );

        userMapper.updateById(new_user);

        return imagePath;
    }
}
