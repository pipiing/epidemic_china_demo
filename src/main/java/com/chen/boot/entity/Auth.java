package com.chen.boot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName auth
 */
@TableName(value ="auth")
@Data
public class Auth implements Serializable {
    /**
     * 序号
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 对应的权限
     */
    private String name;

    /**
     * 展示的名称
     */
    private String title;

    /**
     * 父类的ID
     */
    private Integer pid;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}