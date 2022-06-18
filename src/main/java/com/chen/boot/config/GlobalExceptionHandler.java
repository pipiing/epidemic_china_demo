package com.chen.boot.config;

import com.alibaba.fastjson.JSONObject;
import com.chen.boot.constant.MyConstant;
import com.chen.boot.exception.RegisterFailedException;
import com.chen.boot.exception.ResetPwdFailedException;
import com.chen.boot.exception.UploadFileFailedException;
import com.chen.boot.utils.RequestUtil;
import com.chen.boot.vo.ResultEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理整个Web Controller的异常
 *
 * @author Forget_chen
 * @className GlobalExceptionHandler
 * @desc
 * @date 2022/6/10 10:52
 */
@ControllerAdvice // 将当前类标识为异常处理的组件(基于注解的类)
public class GlobalExceptionHandler {

    /**
     * 用于处理注册时抛出的异常
     * 1、用户名已经被注册异常
     * 2、电话号码已经被注册异常
     * 3、两次密码验证不一致异常
     * @throws IOException
     */
    @ExceptionHandler(RegisterFailedException.class)
    public ModelAndView resolveRegisterException(Exception ex, HttpServletRequest request,
                                                 HttpServletResponse response) throws IOException {
        String viewName = ""; // ajax请求无需页面跳转
        return commonResolver(viewName, ex, request, response);
    }

    /**
     * 用于处理重置密码时抛出的异常
     * @throws IOException
     */
    @ExceptionHandler(ResetPwdFailedException.class)
    public ModelAndView resolveResetPwdFailedException(Exception ex, HttpServletRequest request,
                                                 HttpServletResponse response) throws IOException {
        String viewName = ""; // ajax请求无需页面跳转
        return commonResolver(viewName, ex, request, response);
    }

    /**
     * 用于处理Oss上传文件时抛出的异常
     * @throws IOException
     */
    @ExceptionHandler(UploadFileFailedException.class)
    public ModelAndView resolveUploadFileFailedException(Exception ex, HttpServletRequest request,
                                                       HttpServletResponse response) throws IOException {
        String viewName = ""; // ajax请求无需页面跳转
        return commonResolver(viewName, ex, request, response);
    }
    /**
     * 将重复的代码进行提取，创建通用的方法
     *
     * @param viewName 处理对应异常所要跳转的页面，视图名称
     * @param ex       实际捕获到的异常类型
     * @param request  当前请求对象
     * @param response 当前响应对象
     * @return 返回一个视图，完成跳转
     * @throws IOException
     */
    private ModelAndView commonResolver(String viewName, Exception ex, HttpServletRequest request,
                                        HttpServletResponse response) throws IOException {
        ModelAndView mav = new ModelAndView();
        // 1、判断当前请求的类型
        boolean judgeRequest = RequestUtil.judgeRequestType(request);
        // 2、如果是Ajax请求，不会进行页面跳转
        if (judgeRequest) {
            ResultEntity<Object> resultEntity = ResultEntity.failed(ex.getMessage());
            // 3、将resultEntity对象转换成Json字符串
            String ExJson = JSONObject.toJSONString(resultEntity);
            // 4、将Json字符串作为响应体返回给页面显示
            response.getWriter().write(ExJson);
            return null;
        }
        // 6、如果是普通请求，将异常信息传入model，并且返回视图名称
        mav.addObject(MyConstant.ATTR_NAME_EXCEPTION, ex.getMessage());
        mav.setViewName(viewName);
        // 7、返回ModelAndView对象
        return mav;
    }

}
