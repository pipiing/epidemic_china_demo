package com.chen.boot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName nocv_global_data
 */
@TableName(value ="nocv_global_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalDataBean implements Serializable {
    /**
     * 序号
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Integer id;

    /**
     * 国家名称
     */
    private String name;

    /**
     * 累计确诊总人数
     */
    private Integer confirm;

    /**
     * 新增确诊人数
     */
    private Integer confirmAdd;

    /**
     * 现有确诊人数
     */
    private Integer nowConfirm;

    /**
     * 较上日现有确诊人数
     */
    private Integer nowConfirmCompare;

    /**
     * 累计死亡总人数
     */
    private Integer dead;

    /**
     * 较上日累计死亡人数
     */
    private Integer deadCompare;

    /**
     * 累计治愈总人数
     */
    private Integer heal;

    /**
     * 较上日累计治愈人数
     */
    private Integer healCompare;

    /**
     * 洲名
     */
    private String continent;

    /**
     * 更新时间
     */
    private String date;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}