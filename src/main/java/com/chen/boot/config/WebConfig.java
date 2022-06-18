package com.chen.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Forget_chen
 * @className Webconfig
 * @desc
 * @date 2022/5/29 21:44
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 注入加密方式
    @Bean
    public BCryptPasswordEncoder getBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 注入指定的国际化资源文件
     */
    @Bean
    public ReloadableResourceBundleMessageSource messageSource(){
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.addBasenames("classpath:messages");
        // 修改编码方式：UTF-8 提示信息返回中文
        reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");
        return reloadableResourceBundleMessageSource;
    }

    /**
     * 新增拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

    }



}
