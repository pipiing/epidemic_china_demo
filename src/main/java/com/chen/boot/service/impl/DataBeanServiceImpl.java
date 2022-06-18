package com.chen.boot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.boot.entity.DataBean;
import com.chen.boot.service.DataBeanService;
import com.chen.boot.mapper.DataBeanMapper;
import org.springframework.stereotype.Service;

/**
* @author chen
* @description 针对表【nocv_china_data】的数据库操作Service实现
* @createDate 2022-06-03 14:24:22
*/
@Service
public class DataBeanServiceImpl extends ServiceImpl<DataBeanMapper, DataBean>
    implements DataBeanService{

}




