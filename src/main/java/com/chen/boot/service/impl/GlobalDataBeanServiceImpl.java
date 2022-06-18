package com.chen.boot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.boot.entity.GlobalDataBean;
import com.chen.boot.service.GlobalDataBeanService;
import com.chen.boot.mapper.GlobalDataBeanMapper;
import org.springframework.stereotype.Service;

/**
* @author chen
* @description 针对表【nocv_global_data】的数据库操作Service实现
* @createDate 2022-06-08 13:54:57
*/
@Service
public class GlobalDataBeanServiceImpl extends ServiceImpl<GlobalDataBeanMapper, GlobalDataBean>
    implements GlobalDataBeanService{

}




