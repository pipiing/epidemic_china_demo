package com.chen.boot.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName admin
 */
@TableName(value ="admin")
@Data
public class Admin implements Serializable {
    /**
     * 序号ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 登录账号
     */
    private String userName;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 性别
     */
    private String gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 生日日期
     */
    private String birthday;

    /**
     * 家庭住址
     */
    private String address;

    /**
     * 头像url
     */
    private String avatarUrl;

    /**
     * 手机电话
     */
    private String phone;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}