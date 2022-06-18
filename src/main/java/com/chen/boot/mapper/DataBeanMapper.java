package com.chen.boot.mapper;
import org.apache.ibatis.annotations.Param;

import com.chen.boot.entity.DataBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
* @author chen
* @description 针对表【nocv_china_data】的数据库操作Mapper
* @createDate 2022-06-03 14:24:22
* @Entity com.chen.boot.entity.DataBean
*/
@Repository
public interface DataBeanMapper extends BaseMapper<DataBean> {

}




