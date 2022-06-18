package com.chen.boot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName menu
 */
@TableName(value ="menu")
@Data
public class Menu implements Serializable {
    /**
     * 序号
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 父类ID
     */
    private Integer pid;

    /**
     * 属性类型（menu、button等）
     */
    private String type;

    /**
     * 标题（权限管理等）
     */
    private String title;

    /**
     * 显示图标（class、url等）
     */
    private String icon;

    /**
     * 跳转地址
     */
    private String url;

    /**
     * 是否可用
     */
    private Integer available;

    /**
     * 是否打开
     */
    private Integer open;

    /**
     * 排序数值
     */
    private Integer orderNum;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}