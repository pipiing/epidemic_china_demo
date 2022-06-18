package com.chen.boot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.boot.constant.MyConstant;
import com.chen.boot.entity.DataBean;
import com.chen.boot.entity.HealthClock;
import com.chen.boot.service.HealthClockService;
import com.chen.boot.vo.HealthClockVo;
import com.chen.boot.vo.ResultEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author Forget_chen
 * @className HealthCloRestminController
 * @desc
 * @date 2022/6/6 15:36
 */
@Slf4j
@RestController
public class HealthClockAdminController {

    @Autowired
    private HealthClockService healthClockServiceImpl;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 查询所有打卡记录，带有模糊查询条件 带有分页功能
     *
     * @param healthClockVo
     * @return
     */
    @RequestMapping("/list/healthClock/by/page")
    public ResultEntity<Object> listHealthClockByPage(HealthClockVo healthClockVo) {
        Page<HealthClock> page = new Page<>(healthClockVo.getPage(), healthClockVo.getLimit());

        // 封装查询条件：模糊查询"user_name"，电话号码eq判断
        LambdaQueryWrapper<HealthClock> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotBlank(healthClockVo.getUserName()), HealthClock::getUserName,
                healthClockVo.getUserName());
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(healthClockVo.getPhone()), HealthClock::getPhone,
                healthClockVo.getPhone());

        healthClockServiceImpl.page(page, lambdaQueryWrapper);

        return ResultEntity.successWithData(MyConstant.LAYUI_SUCCESS_CODE, page.getTotal(), page.getRecords());
    }

    /**
     * 新增健康打卡记录数据
     *
     * @param healthClock
     * @return
     */
    @PreAuthorize("hasAuthority('clock:add')")
    @RequestMapping("/healthClock/add")
    public ResultEntity<Object> healthClockAddOrUpdate(HealthClock healthClock) {
        // 将Data类型转换为指定格式的字符串使用sdf
        healthClock.setCreateTime(sdf.format(new Date()));
        boolean flag = healthClockServiceImpl.save(healthClock);
        if (flag) {
            return ResultEntity.successWithOutData();
        }
        return ResultEntity.failed("新增数据失败～～");
    }

    /**
     * 根据用户ID删除对应打卡数据
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAuthority('clock:delete')")
    @RequestMapping("/healthClock/delete/by/id")
    public ResultEntity<Object> healthClockDeleteById(@RequestParam("id") Integer id) {
        boolean flag = healthClockServiceImpl.removeById(id);
        if (flag) {
            return ResultEntity.successWithOutData();
        }
        return ResultEntity.failed("删除数据失败～～");
    }

    /**
     * 根据用户ID更新对应打卡数据
     *
     * @param healthClock 需要更新的内容
     * @return
     */
    @PreAuthorize("hasAuthority('clock:update')")
    @RequestMapping("/healthClock/edit/by/id")
    public ResultEntity<Object> healthClockEditById(HealthClock healthClock) {
        // 将Data类型转换为指定格式的字符串使用sdf
        healthClock.setCreateTime(sdf.format(new Date()));
        boolean flag = healthClockServiceImpl.saveOrUpdate(healthClock);
        if (flag) {
            return ResultEntity.successWithOutData();
        }
        return ResultEntity.failed("修改数据失败～～");
    }

    /**
     * Excel的拖拽或者点击上传文件
     * 1、前台页面发送一个请求，上传文件 MultipartFile Http
     * 2、Controller，使用 MultipartFile 类型的参数接收对应文件
     * 3、使用poi解析文件，将里面的数据一条条全部解析出来
     * 4、然后一条条的 存储到数据库
     */
    @RequestMapping("/healthClock/import/excel")
    public ResultEntity<Object> chinaImportExcel(@RequestParam("file") MultipartFile file) throws IOException {
        // 1、判断文件是否为空（为空无效）
        if (file.isEmpty()) {
            return ResultEntity.failed("文件数据为空，无法上传");
        }
        // 2、使用poi获取Excel解析数据
        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        // 获取到工作表1
        XSSFSheet sheet1 = wb.getSheetAt(0);
        // 创建一个List<DataBean>集合，用于存储 解析后的工作表中的数据
        List<HealthClock> healthClockList = new ArrayList<>();
        // 遍历工作表，存储到集合中
        for (Row cell : sheet1) {
            String name = cell.getCell(0).getStringCellValue(); // 姓名
            String gender = cell.getCell(1).getStringCellValue(); // 性别
            Integer age = (int) cell.getCell(2).getNumericCellValue(); // 年龄
            String phone = cell.getCell(3).getStringCellValue(); // 电话
            String morningTemp = cell.getCell(4).getStringCellValue(); // 早上体温
            String afternoonTemp = cell.getCell(5).getStringCellValue(); // 下午体温
            String nightTemp = cell.getCell(6).getStringCellValue(); // 晚上体温
            String feverAndCoug = cell.getCell(7).getStringCellValue(); // 发烧咳嗽
            String recentHome = cell.getCell(8).getStringCellValue(); // 近期居住地
            String riskZone = cell.getCell(9).getStringCellValue(); // 风险地区
            String recentZone = cell.getCell(10).getStringCellValue(); // 近期接触地
            String healthStatus = cell.getCell(11).getStringCellValue(); // 健康状态
            String date = sdf.format(new Date()); // 打卡时间

            // 定义DataBean实体类用于接收数据
            HealthClock healthClock = new HealthClock(null, name, gender, age, phone, morningTemp, afternoonTemp,
                    nightTemp, feverAndCoug, recentHome, riskZone, recentZone, healthStatus, date);
            healthClockList.add(healthClock);
        }

        // 3、插入数据库，实现批量插入
        boolean flag = healthClockServiceImpl.saveBatch(healthClockList);
        if (flag) {
            return ResultEntity.successWithOutData();
        }
        return ResultEntity.failed("'上传excel文件失败!' ");
    }

}
