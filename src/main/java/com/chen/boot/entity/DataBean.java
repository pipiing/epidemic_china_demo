package com.chen.boot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName nocv_china_data
 */
@TableName(value ="nocv_china_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataBean implements Serializable {
    /**
     * 数据库中的序号
     */
    @TableId(type=IdType.ASSIGN_ID)
    private Integer id;

    /**
     * 地区
     */
    private String area;

    /**
     * 总确诊人数
     */
    private Integer nowConfirm;

    /**
     * 总死亡人数
     */
    private Integer dead;

    /**
     * 总治愈人数
     */
    private Integer heal;

    /**
     * 数据时间
     */
    private String date;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}