package com.chen.boot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName nocv_china_addData
 */
@TableName(value ="nocv_china_addData")
@Data
public class AddDataBean implements Serializable {
    /**
     * 序号
     */
    @TableId(type=IdType.ASSIGN_ID)
    private Integer id;

    /**
     * 地区
     */
    private String area;

    /**
     * 新增确诊人数
     */
    @TableField("confirm_add")
    private Integer confirmAdd;

    /**
     * 新增死亡人数
     */
    private Integer dead;

    /**
     * 新增治愈人数
     */
    private Integer heal;

    /**
     * 新增无症状人数
     */
    private Integer zero;

    /**
     * 现确诊人数
     */
    @TableField("now_confirm")
    private Integer nowConfirm;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}