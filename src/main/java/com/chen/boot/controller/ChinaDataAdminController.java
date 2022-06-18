package com.chen.boot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.boot.constant.MyConstant;
import com.chen.boot.entity.DataBean;
import com.chen.boot.mapper.DataBeanMapper;
import com.chen.boot.service.DataBeanService;
import com.chen.boot.vo.DataBeanVo;
import com.chen.boot.vo.ResultEntity;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Forget_chen
 * @className ChinaDataAdminController
 * @desc
 * @date 2022/6/4 09:38
 */
@RestController
public class ChinaDataAdminController {

    @Autowired
    private DataBeanMapper dataBeanMapper;

    @Autowired
    private DataBeanService dataBeanServiceImpl;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * @param dataBeanVo 继承了DataBean，多了page和limit属性
     * @return
     */
    @GetMapping("/list/data/by/page")
    public ResultEntity<Object> listDataByPage(DataBeanVo dataBeanVo) {
        // 1、创建 Page 分页对象
        Page<DataBean> page = new Page<>(dataBeanVo.getPage(), dataBeanVo.getLimit());

        // 2、创建模糊查询 LambdaQueryWrapper 对象
        LambdaQueryWrapper<DataBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 如果传递的Area属性不为空，则进行模糊查询，反之不进行查询
        lambdaQueryWrapper.like(StringUtils.isNotBlank(dataBeanVo.getArea()), DataBean::getArea, dataBeanVo.getArea());

        // 3、按照确诊病例人数进行倒序排序（从大到小）
        lambdaQueryWrapper.orderByDesc(DataBean::getNowConfirm);
        lambdaQueryWrapper.select(DataBean::getId, DataBean::getArea, DataBean::getNowConfirm, DataBean::getDead,
                DataBean::getHeal, DataBean::getDate);

        // 4、查询数据库
        dataBeanMapper.selectPage(page, lambdaQueryWrapper);

        // 6、请求成功，携带参数：总共数据条数、数据 返回类型：Object类型
        return ResultEntity.successWithData(MyConstant.LAYUI_SUCCESS_CODE, page.getTotal(), page.getRecords());
    }

    /**
     * 根据id删除对应疫情数据
     *
     * @param id 数据库中的序号
     * @return 不携带数据
     */
    @PreAuthorize("hasAuthority('china:delete')")
    @RequestMapping("/china/delete/by/id")
    public ResultEntity<String> chinaDeleteById(@RequestParam("id") Integer id) {
        LambdaQueryWrapper<DataBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DataBean::getId, id);
        // 执行根据 area 删除对应疫情数据
        int deleteCount = dataBeanMapper.delete(lambdaQueryWrapper);
        // 执行完SQL语句，进行判断是否完成删除功能
        if (deleteCount == 0) {
            return ResultEntity.failed("删除数据失败～～");
        }

        return ResultEntity.successWithOutData();
    }

    /**
     * 添加疫情数据
     *
     * @param dataBean 表单提交的数据
     */
    @PreAuthorize("hasAuthority('china:update') or hasAuthority('china:add')")
    @RequestMapping("/china/addOrUpdate")
    public ResultEntity<String> addChina(DataBean dataBean) {
        LambdaQueryWrapper<DataBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 1、判断area在数据库中是否存在，存在执行更新操作，否则执行新增操作
        lambdaQueryWrapper.eq(DataBean::getArea, dataBean.getArea());
        List<DataBean> dataBeanList = dataBeanMapper.selectList(lambdaQueryWrapper);
        if (dataBeanList == null || dataBeanList.size() == 0) {
            // 不存在对应的area，执行新增操作
            dataBean.setDate(sdf.format(new Date()));
            int insertCount = dataBeanMapper.insert(dataBean);
            // 执行完SQL语句，进行判断是否完成新增功能
            if (insertCount == 0) {
                return ResultEntity.failed("新增数据失败～～");
            }
        } else {
            // 执行完SQL语句，进行判断是否完成新增功能
            int updateCount = dataBeanMapper.update(dataBean, lambdaQueryWrapper);
            if (updateCount == 0) {
                return ResultEntity.failed("更新数据失败～～");
            }
        }

        return ResultEntity.successWithOutData();
    }

    /**
     * Excel的拖拽或者点击上传文件
     * 1、前台页面发送一个请求，上传文件 MultipartFile Http
     * 2、Controller，使用 MultipartFile 类型的参数接收对应文件
     * 3、使用poi解析文件，将里面的数据一条条全部解析出来
     * 4、然后一条条的 存储到数据库
     */
    @RequestMapping("/china/import/excel")
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
        List<DataBean> dataBeanList = new ArrayList<>();
        // 遍历工作表，存储到集合中
        for (Row cell : sheet1) {
            String area = cell.getCell(0).getStringCellValue(); // 省份名称
            Integer nowConfirm = (int) cell.getCell(1).getNumericCellValue(); // 总确诊人数
            Integer dead = (int) cell.getCell(2).getNumericCellValue(); // 总死亡人数
            Integer heal = (int) cell.getCell(3).getNumericCellValue(); // 总治愈人数
            String date = sdf.format(new Date()); // 更新时间

            // 定义DataBean实体类用于接收数据
            DataBean dataBean = new DataBean(null, area, nowConfirm, dead, heal, date);
            dataBeanList.add(dataBean);
        }

        // 3、插入数据库，实现批量插入
        boolean flag = dataBeanServiceImpl.saveBatch(dataBeanList);
        if (flag) {
            return ResultEntity.successWithOutData();
        }
        return ResultEntity.failed("'上传excel文件失败!' ");
    }


}
