package com.chen.boot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.boot.entity.Menu;
import com.chen.boot.service.MenuService;
import com.chen.boot.mapper.MenuMapper;
import org.springframework.stereotype.Service;

/**
* @author chen
* @description 针对表【menu】的数据库操作Service实现
* @createDate 2022-06-14 14:24:00
*/
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu>
    implements MenuService{

}




