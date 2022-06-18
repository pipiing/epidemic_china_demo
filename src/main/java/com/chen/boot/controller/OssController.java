package com.chen.boot.controller;

import com.chen.boot.service.OssService;
import com.chen.boot.vo.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Forget_chen
 * @className OssController
 * @desc
 * @date 2022/6/11 14:34
 */
@RestController
@CrossOrigin // 防止跨域
public class OssController {

    @Autowired
    private OssService ossServiceImpl; // 用于上传头像到阿里云的oss

    // 上传头像方法
    @PostMapping("/oss/upload/img")
    public ResultEntity<String> OssUploadImage(MultipartFile file){
        // 获取上传的文件 MultipartFile
        // 返回值：上传到Oss的路径
        String avatar_url = ossServiceImpl.uploadFileAvatar(file);

        return ResultEntity.successWithData(avatar_url);
    }

}
