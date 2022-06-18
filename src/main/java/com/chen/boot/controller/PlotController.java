package com.chen.boot.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chen.boot.entity.AddDataBean;
import com.chen.boot.entity.DataBean;
import com.chen.boot.entity.GlobalDataBean;
import com.chen.boot.service.AddDataBeanService;
import com.chen.boot.service.DataBeanService;
import com.chen.boot.service.GlobalDataBeanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Forget_chen
 * @className IndexController
 * @desc
 * @date 2022/6/2 15:36
 */
@Slf4j
@RestController
public class PlotController {
    @Autowired
    private AddDataBeanService AddDataBeanServiceImpl; // 用于操作nocv_AddData表
    @Autowired
    private DataBeanService dataBeanServiceImpl; // 用于操作nocv_data表

    @Autowired
    private GlobalDataBeanService globalDataBeanServiceImpl;

    private final Jedis jedis = new Jedis(); // 用于操作Redis的客户端工具


    /**
     * 查询中国确诊疫情数据
     *
     * @return 从数据库中查询nocv_data表中的area和now_confirm数据返回
     */
    @RequestMapping("/query/all")
    public List<DataBean> queryAllData() {
        // 1、查询Redis缓存中，是否有中国疫情数据
        jedis.auth("202428");
        List<String> redisChinaDataList = jedis.lrange("china_data", 0, -1);

        List<DataBean> dataBeanList = new ArrayList<>();
        if (redisChinaDataList != null && redisChinaDataList.size() > 0) {

            // 1.1 Redis中有缓存直接返回【将Json字符串转换为Json对象】
            for (String redisChinaData : redisChinaDataList) {
                JSONObject jsonObject = JSONObject.parseObject(redisChinaData);

                DataBean dataBean = new DataBean();
                String area = (String) jsonObject.get("area");
                Integer nowConfirm = (Integer) jsonObject.get("nowConfirm");
                dataBean.setArea(area);
                dataBean.setNowConfirm(nowConfirm);
                dataBeanList.add(dataBean);
                System.out.println(dataBean);
            }
        } else {
            // 1.2 Redis中没有缓存，查询MySQL数据库，更新缓存
            LambdaQueryWrapper<DataBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.select(DataBean::getArea, DataBean::getNowConfirm);
            dataBeanList = dataBeanServiceImpl.list(lambdaQueryWrapper);
            for (DataBean dataBean : dataBeanList) {
                // 更新缓存
                jedis.lpush("china_data", JSONObject.toJSONString(dataBean));
            }
        }

        return dataBeanList;
    }

    /**
     * 查询中国新增疫情数据
     *
     * @return 从数据库中查询nocv_data表中的area和now_confirm数据返回
     */
    @RequestMapping("/query/add/all")
    public List<AddDataBean> queryAddAllData() {
        LambdaQueryWrapper<AddDataBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(AddDataBean::getArea, AddDataBean::getConfirmAdd);
        return AddDataBeanServiceImpl.list(lambdaQueryWrapper);
    }


    /**
     * 查询全球现确诊疫情数据
     *
     * @return 从数据库中查询nocv_global_data表中的name和now_confirm数据返回
     */
    @RequestMapping("/query/global/all")
    public List<GlobalDataBean> queryGlobalAllData() {
        LambdaQueryWrapper<GlobalDataBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(GlobalDataBean::getName, GlobalDataBean::getNowConfirm);
        return globalDataBeanServiceImpl.list(lambdaQueryWrapper);
    }

    /**
     * 查询全球现确诊疫情数据
     *
     * @return 从数据库中查询nocv_global_data表中的name和confirm_add数据返回
     */
    @RequestMapping("/query/global/add/all")
    public List<GlobalDataBean> queryGlobalAddAllData() {
        LambdaQueryWrapper<GlobalDataBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(GlobalDataBean::getName, GlobalDataBean::getConfirmAdd);
        return globalDataBeanServiceImpl.list(lambdaQueryWrapper);
    }


}
