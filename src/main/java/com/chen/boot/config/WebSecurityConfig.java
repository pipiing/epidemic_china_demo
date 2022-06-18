package com.chen.boot.config;

import com.chen.boot.handler.MyAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 * @author Forget_chen
 * @className WebSecurityConfig
 * @desc
 * @date 2022/6/8 17:30
 */
@Configuration // 标识为配置类
@EnableWebSecurity // 开启web环境下权限控制功能
@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启全局方法权限控制功能 prePostEnabled = true（保证@PreAuthority、@PostAuthority、PostFilter注解生效）
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private AccessDeniedHandler myAccessDeniedHandler;
    @Autowired
    private AdminUserDetailsService adminUserDetailsServiceImpl;
    @Autowired
    private DataSource dataSource;

    //用于设置rememberMe往数据库存储token
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource); // 设置数据源
        tokenRepository.setCreateTableOnStartup(false); // 启动时，无需再次创建数据表
        return tokenRepository;
    }

    /**
     * 对资源进行授权
     */
    @Override
    protected void configure(HttpSecurity security) throws Exception {
        // 页面访问授权
        security.authorizeRequests()
                .antMatchers("/to/login").permitAll() // 对登录页面进行放行
                .antMatchers("/to/register").permitAll() // 对注册页面进行放行
                .antMatchers("/do/register").permitAll() // 对注册请求进行放行
                .antMatchers("/do/resetPwd").permitAll() // 对重置密码请求进行放行
                .and()
                .authorizeRequests().anyRequest().authenticated() // 针对其他任何请求都需要登录后才能访问
        ;

        // 实现登录验证功能
        security
                .csrf().disable() // 禁用CSRF功能
                .formLogin()
                .loginPage("/to/login") // 指定登录页面请求地址
                .loginProcessingUrl("/security/do/login") // 指定提交登录表单请求，不能和Controller中的映射方式一致
                .defaultSuccessUrl("/") // 登录成功后，跳转到对应的页面
        ;

        // 实现注销功能
        security.logout()
                .logoutUrl("/security/do/logout") // 指定提交注销请求
                .logoutSuccessUrl("/") // 退出成功返回登录页面
        ;

        // 实现记住我数据库版
        security.rememberMe()
                .tokenRepository(persistentTokenRepository())
        ;

        // 自定义异常处理器
        security.exceptionHandling()
                .accessDeniedHandler(myAccessDeniedHandler)

        ;

    }

    /**
     * 用户认证
     * 在SpringBoot 2.1.x 可以直接使用明文密码
     * 在SpringSecurity 5.0+版本中 必须要密码加密：PasswordEncoder
     */
    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        // 使用数据库版本实现登录
        builder.userDetailsService(adminUserDetailsServiceImpl)
                .passwordEncoder(bCryptPasswordEncoder) // 使用BCrypt加密方式对用户密码进行处理
        ;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 解决静态资源被拦截的问题
        web.ignoring().antMatchers("/css/**","/fonts/**","/images/**","/js/**","/**.ico","/echarts/**","/layui/**");
    }


}
