package com.chen.boot.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * 常用工具类
 *
 * @author Forget_chen
 * @className CommonUtil
 * @desc
 * @date 2022/6/16 21:40
 */
public class CommonUtil {

    /**
     * 用于循环判断传入的参数是否全部都不为空【null】
     *
     * @param args 可变长参数【可以接受 0 个或者多个参数】
     * @return
     */
    public static boolean isAllNotBlank(String... args) {
        for (String arg : args) {
            if (!StringUtils.isNotBlank(arg)) {
                return false;
            }
        }
        return true;
    }

}
