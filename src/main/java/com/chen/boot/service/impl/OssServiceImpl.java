package com.chen.boot.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.chen.boot.exception.UploadFileFailedException;
import com.chen.boot.service.OssService;
import com.chen.boot.utils.ConstantPropertiesUtil;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * @author Forget_chen
 * @className OssServiceImpl
 * @desc
 * @date 2022/6/11 14:34
 */
@Service
public class OssServiceImpl implements OssService {
    /**
     * 上传头像到Oss中
     *
     * @param file
     * @return
     */
    @Override
    public String uploadFileAvatar(MultipartFile file) {
        // 1、通过工具类获取对应的值
        String endpoint = ConstantPropertiesUtil.END_POINT;
        String accessKeyId = ConstantPropertiesUtil.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtil.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtil.BUCKET_NAME;

        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 获取上传文件的输入流
            InputStream inputStream = file.getInputStream();

            // 1、利用UUID生成文件名
            String fileName = file.getOriginalFilename();
            String suffix = "";
            if (StringUtils.isBlank(fileName)) {
                throw new UploadFileFailedException("参数错误，文件名为空！");
            }
            String[] split = fileName.split("\\.");
            suffix = split[split.length - 1];

            fileName = UUID.randomUUID().toString().replace("-", "") + "." + suffix;

            // 2、把文件按照日期（年/月/日）分类  2022/6/11/1.jpg
            String datePath = new DateTime().toString("yyyy/MM/dd");
            fileName = datePath + "/" + fileName;

            /**
             * 调用oss对象中的 putObject 实现上传
             * ossClient.putObject(bucketName, objectName, inputStream);
             * 参数一：Bucket名称
             * 参数二：上传到Oss文件路径和文件名称
             * 参数三：文件的输入流
             */
            ossClient.putObject(bucketName, fileName, inputStream);

            // 把上传之后的文件路径返回，需要手动拼接
            return "https://" + bucketName + "." + endpoint + "/" + fileName;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            // 关闭OSS对象
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
