package com.chen.boot.service;

import org.springframework.web.multipart.MultipartFile;

public interface OssService {

    String uploadFileAvatar(MultipartFile file);

}
