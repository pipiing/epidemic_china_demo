package com.chen.boot.handler;

import com.alibaba.fastjson.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Forget_chen
 * @className MyAccessDeniedHandler
 * @desc
 * @date 2022/6/16 14:22
 */
@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {
    private String errorPage;

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        // 判断是否为ajax请求
        if (httpServletRequest.getHeader("accept").contains("application/json")
                || (httpServletRequest.getHeader("X-Requested-With") != null && httpServletRequest.getHeader("X-Requested-With").equals("XMLHttpRequest"))) {
            // 设置状态为403，无权限状态
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            // 设置格式以及返回json数据 方便前台使用responseJSON接取
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json; charset=utf-8");
            // 1、创建一个Json对象
            JSONObject json = new JSONObject();
            // 2、设置错误提示信息
            json.put("msg","权限不足，请联系管理员");
            // 3、将Json对象转换成Json字符串
            String ExJson = JSONObject.toJSONString(json);
            // 4、将Json字符串作为响应体返回给页面显示
            httpServletResponse.getWriter().write(ExJson);
        }else if(!httpServletResponse.isCommitted()){ // 非ajax请求
            if(errorPage!=null){
                // Put exception into request scope (perhaps of use to a view)
                httpServletRequest.setAttribute(WebAttributes.ACCESS_DENIED_403, e);

                // Set the 403 status code.
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

                // forward to error page.
                RequestDispatcher dispatcher = httpServletRequest.getRequestDispatcher(errorPage);
                dispatcher.forward(httpServletRequest, httpServletResponse);
            }else{
                httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN,e.getMessage());
            }
        }
    }


    public void setErrorPage(String errorPage) {
        if ((errorPage != null) && !errorPage.startsWith("/")) {
            throw new IllegalArgumentException("errorPage must begin with '/'");
        }
        this.errorPage = errorPage;
    }

}
