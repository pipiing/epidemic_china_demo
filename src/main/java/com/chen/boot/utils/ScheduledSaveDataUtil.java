package com.chen.boot.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chen.boot.entity.AddDataBean;
import com.chen.boot.entity.DataBean;
import com.chen.boot.entity.GlobalDataBean;
import com.chen.boot.service.AddDataBeanService;
import com.chen.boot.service.DataBeanService;
import com.chen.boot.service.GlobalDataBeanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Forget_chen
 * @className ScheduledSaveDataUtil
 * @desc
 * @date 2022/6/3 09:47
 */
@Slf4j
@Component
public class ScheduledSaveDataUtil {

    @Autowired
    private AddDataBeanService AddDataBeanServiceImpl; // 操作中国新增疫情数据表

    @Autowired
    private DataBeanService dataBeanServiceImpl; // 操作中国疫情数据表

    @Autowired
    private GlobalDataBeanService globalDataBeanServiceImpl; // 操作全球疫情数据表


    private static final String addDataUrl = "https://api.inews.qq.com/newsqa/v1/query/inner/publish/modules/list" +
            "?modules=nowConfirmStatis,provinceCompare";

    private static final String dataUrl = "https://api.inews.qq.com/newsqa/v1/query/inner/publish/modules/list" +
            "?modules=localCityNCOVDataList,diseaseh5Shelf";

    private static final String globalDataUrl = "https://api.inews.qq.com/newsqa/v1/automation/modules/list?modules" +
            "=FAutoCountryConfirmAdd,WomWorld,WomAboard";

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Scheduled(cron = "0 0 0/6 * * ?")
    public void updateData() {
        log.info("更新疫情数据～～");
        saveData();
    }

    public void saveData() {
        try {
            List<AddDataBean> addDataBeans = getChinaAddData();
            List<DataBean> dataBeans = getChinaData();
            List<GlobalDataBean> globalDataBeans = getGlobalData();

            // 先将数据清空  然后存储数据
            dataBeanServiceImpl.remove(null);
            dataBeanServiceImpl.saveBatch(dataBeans);

            AddDataBeanServiceImpl.remove(null);
            AddDataBeanServiceImpl.saveBatch(addDataBeans);

            globalDataBeanServiceImpl.remove(null);
            globalDataBeanServiceImpl.saveBatch(globalDataBeans);

            // 每次数据更新需要删除掉Redis中的缓存
            Jedis jedis = new Jedis();
            jedis.auth("202428");
            if (jedis!=null){
                // 清空数据库【0号】
                jedis.flushDB();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 实时获取中国新增疫情数据，将解析的数据封装到AddDataBean中
     */
    public static List<AddDataBean> getChinaAddData() {
        // 实时获取数据
        String respJson = HttpClientUtil.doGet(addDataUrl);

        // 使用FastJson将字符串转换为Json数据
        JSONObject parseObj = JSON.parseObject(respJson);
        String data = parseObj.getString("data");
        JSONObject parseData = JSON.parseObject(data);

        String provinceCompare = parseData.getString("provinceCompare");
        // 将字符串转换成Json对象
        JSONObject parseProvinceCompare = JSON.parseObject(provinceCompare);
        // 遍历存储到List<DataBean>集合中
        Set<Map.Entry<String, Object>> entries = parseProvinceCompare.entrySet();

        List<AddDataBean> dataList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : entries) {
            AddDataBean dataBean = new AddDataBean();

            // 封装地区名称
            dataBean.setArea(entry.getKey());
            // 将Value值转换为Json对象，通过getInteger获取对应的值，进行封装
            JSONObject parseValue = JSON.parseObject(entry.getValue().toString());

            // 封装对应的疫情数据
            dataBean.setConfirmAdd(parseValue.getInteger("confirmAdd"));
            dataBean.setDead(parseValue.getInteger("dead"));
            dataBean.setHeal(parseValue.getInteger("heal"));
            dataBean.setZero(parseValue.getInteger("zero"));
            dataBean.setNowConfirm(parseValue.getInteger("nowConfirm"));

            dataList.add(dataBean);
        }

        return dataList;
    }

    /**
     * 实时获取中国疫情数据，将解析的数据封装到DataBean中
     *
     * @return
     */
    public static List<DataBean> getChinaData() {
        // 实时获取数据
        String respJson = HttpClientUtil.doGet(dataUrl);

        JSONObject parseObj = JSON.parseObject(respJson);
        String data = parseObj.getString("data");

        JSONObject parseData = JSON.parseObject(data);
        String diseaseh5Shelf = parseData.getString("diseaseh5Shelf");

        JSONObject parseDis = JSON.parseObject(diseaseh5Shelf);
        String areaTree = parseDis.getString("areaTree");

        JSONObject parseObject = (JSONObject) JSON.parseArray(areaTree).get(0);
        String children = parseObject.getString("children");

        // 创建List<DataBean>的List集合
        List<DataBean> dataBeanList = new ArrayList<>();

        JSONArray parseChildrenList = JSON.parseArray(children);
        for (Object parseChildren : parseChildrenList) {
            String area = ((JSONObject) parseChildren).getString("name");
            String date = sdf.format(new Date());

            String total = ((JSONObject) parseChildren).getString("total");
            JSONObject parseTotal = JSON.parseObject(total);

            Integer nowConfirm = parseTotal.getInteger("nowConfirm");
            Integer dead = parseTotal.getInteger("dead");
            Integer heal = parseTotal.getInteger("heal");

            // 将数据封装到DataBean对象中
            DataBean dataBean = new DataBean(null, area, nowConfirm, dead, heal, date);

            dataBeanList.add(dataBean);
        }

        return dataBeanList;
    }

    /**
     * 实时获取全球疫情数据，将解析的数据封装到GlobalDataBean中
     *
     * @return
     */
    public static List<GlobalDataBean> getGlobalData() {
        // 实时获取数据
        String respJson = HttpClientUtil.doGet(globalDataUrl);

        JSONObject parseObj = JSON.parseObject(respJson);
        String data = parseObj.getString("data");

        JSONObject parseData = JSON.parseObject(data);
        String womAboard = parseData.getString("WomAboard");

        JSONArray parseWomAboardList = JSON.parseArray(womAboard);

        // 获取海外疫情数据并封装
        List<GlobalDataBean> globalDataBeanList = new ArrayList<>();
        for (Object parseWomAboard : parseWomAboardList) {
            // 遍历封装GlobalDataBean实体类
            String name = ((JSONObject) parseWomAboard).getString("name");
            Integer confirm = ((JSONObject) parseWomAboard).getInteger("confirm");
            Integer confirmAdd = ((JSONObject) parseWomAboard).getInteger("confirmAdd");
            Integer dead = ((JSONObject) parseWomAboard).getInteger("dead");
            Integer deadCompare = ((JSONObject) parseWomAboard).getInteger("deadCompare");
            Integer heal = ((JSONObject) parseWomAboard).getInteger("heal");
            Integer healCompare = ((JSONObject) parseWomAboard).getInteger("healCompare");
            Integer nowConfirm = ((JSONObject) parseWomAboard).getInteger("nowConfirm");
            Integer nowConfirmCompare = ((JSONObject) parseWomAboard).getInteger("nowConfirmCompare");
            String continent = ((JSONObject) parseWomAboard).getString("continent");
            String date = sdf.format(new Date());

            GlobalDataBean globalDataBean = new GlobalDataBean(null, name, confirm, confirmAdd, nowConfirm,
                    nowConfirmCompare, dead, deadCompare, heal, healCompare, continent, date);

            globalDataBeanList.add(globalDataBean);
        }

        // 实时获取中国疫情数据
        respJson = HttpClientUtil.doGet(dataUrl);

        parseObj = JSON.parseObject(respJson);
        data = parseObj.getString("data");

        parseData = JSON.parseObject(data);
        String diseaseh5Shelf = parseData.getString("diseaseh5Shelf");

        JSONObject parseDis = JSON.parseObject(diseaseh5Shelf);
        String chinaTotal = parseDis.getString("chinaTotal");
        String chinaAdd = parseDis.getString("chinaAdd");

        JSONObject parseChinaTotal = JSON.parseObject(chinaTotal);
        JSONObject parseChinaAdd = JSON.parseObject(chinaAdd);

        String name = "中国";
        Integer confirm = parseChinaTotal.getInteger("confirm");
        Integer confirmAdd = parseChinaTotal.getInteger("confirmAdd");
        Integer dead = parseChinaTotal.getInteger("dead");
        Integer deadCompare = parseChinaAdd.getInteger("dead");
        Integer heal = parseChinaTotal.getInteger("heal");
        Integer healCompare = parseChinaAdd.getInteger("heal");
        Integer nowConfirm = parseChinaTotal.getInteger("nowConfirm");
        Integer nowConfirmCompare = parseChinaAdd.getInteger("nowConfirm");
        String continent = "亚洲";
        String date = sdf.format(new Date());

        GlobalDataBean globalDataBean = new GlobalDataBean(null, name, confirm, confirmAdd, nowConfirm,
                nowConfirmCompare, dead, deadCompare, heal, healCompare, continent, date);

        globalDataBeanList.add(globalDataBean);

        return globalDataBeanList;
    }

    public static void main(String[] args) {
        List<GlobalDataBean> globalData = getGlobalData();
        globalData.forEach(System.out::println);
    }

}
