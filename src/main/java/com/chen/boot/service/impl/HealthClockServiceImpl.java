package com.chen.boot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.boot.entity.HealthClock;
import com.chen.boot.service.HealthClockService;
import com.chen.boot.mapper.HealthClockMapper;
import org.springframework.stereotype.Service;

/**
* @author chen
* @description 针对表【health_clock】的数据库操作Service实现
* @createDate 2022-06-06 15:13:22
*/
@Service
public class HealthClockServiceImpl extends ServiceImpl<HealthClockMapper, HealthClock>
    implements HealthClockService{

}




