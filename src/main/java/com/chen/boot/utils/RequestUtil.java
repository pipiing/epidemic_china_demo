package com.chen.boot.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Forget_chen
 * @className AjaxUtil
 * @desc
 * @date 2022/6/10 11:59
 */
public class RequestUtil {
    /**
     * 判断当前请求是否为Ajax请求
     *
     * @param request 请求对象
     * @return true：当前请求为Ajax请求
     * false：当前请求不是Ajax请求
     */
    public static boolean judgeRequestType(HttpServletRequest request) {
        // 1、从请求头中获取 Accept 和 X-Request-With 的值
        String acceptHeader = request.getHeader("Accept");
        String xRequestHeader = request.getHeader("X-Request-With");
        // 2、判断
        return ((acceptHeader != null && acceptHeader.contains("application/json")) || ("XMLHttpRequest".equals(xRequestHeader)));
    }


}
