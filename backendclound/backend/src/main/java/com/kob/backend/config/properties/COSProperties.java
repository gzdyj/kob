package com.kob.backend.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author ：cjy
 * @description ：cos配置属性类 使用ConfigurationProperties注解可将配置文件（yml/properties）中指定前缀的配置转为bean
 * @CreateTime ：Created in 2019/9/8 23:39
 */
@Data
@ConfigurationProperties
public class COSProperties {
    // 初始化用户身份信息 前往密钥管理查看
    private String secretId = "AKIDLZYWIq9C0ZtypQlriKkneIhsoVxA9uRj";
    // 初始化用户身份信息 前往密钥管理查看
    private String secretKey = "6xZNxIOGtISM0OXS8ZlhOq79wTklW1FF";
    // 指定要上传到的存储桶
    private String bucketName = "images-1256627063";
    //指定要上传的地区名称 去控制台查询
    private String regionName = "ap-guangzhou";
}