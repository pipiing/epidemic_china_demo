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
 * @TableName health_clock
 */
@TableName(value ="health_clock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthClock implements Serializable {
    /**
     * 序号
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 性别
     */
    private String gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 电话
     */
    private String phone;

    /**
     * 早晨体温
     */
    private String morningTemp;

    /**
     * 下午体温
     */
    private String afternoonTemp;

    /**
     * 晚上体温
     */
    private String nightTemp;

    /**
     * 发烧和咳嗽
     */
    private String feverAndCough;

    /**
     * 最近居住地
     */
    private String recentHome;

    /**
     * 低、中、高风险地区
     */
    private String riskZone;

    /**
     * 最近接触地
     */
    private String recentZone;

    /**
     * 健康状态（健康、发烧）
     */
    private String healthStatus;

    /**
     * 更新时间
     */
    private String createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}