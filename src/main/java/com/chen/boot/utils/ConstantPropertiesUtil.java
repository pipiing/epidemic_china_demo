package com.chen.boot.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 专门用于读取application.yaml配置文件中的设置的工具类
 *
 * @author Forget_chen
 * @className ConstantPropertiesUtils
 * @desc
 * @date 2022/6/11 14:20
 */
@Component
public class ConstantPropertiesUtil implements InitializingBean {
    // 使用@Value注解 读取配置文件内容并赋值
    @Value("${aliyun.oss.file.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.file.keyid}")
    private String keyId;

    @Value("${aliyun.oss.file.keysecret}")
    private String keySecret;

    @Value("${aliyun.oss.file.bucketname}")
    private String bucketName;

    // 定义公开静态常量，用于提供给外部使用
    public static String END_POINT;
    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRET;
    public static String BUCKET_NAME;


    /**
     * 当该类被加载到Spring的IOC容器中时，会自动调用以下方法
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        END_POINT = endpoint;
        ACCESS_KEY_ID = keyId;
        ACCESS_KEY_SECRET = keySecret;
        BUCKET_NAME = bucketName;
    }
}
