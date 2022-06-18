package com.chen.boot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.boot.entity.Auth;
import com.chen.boot.service.AuthService;
import com.chen.boot.mapper.AuthMapper;
import org.springframework.stereotype.Service;

/**
* @author chen
* @description 针对表【auth】的数据库操作Service实现
* @createDate 2022-06-13 22:16:29
*/
@Service
public class AuthServiceImpl extends ServiceImpl<AuthMapper, Auth>
    implements AuthService{

}




