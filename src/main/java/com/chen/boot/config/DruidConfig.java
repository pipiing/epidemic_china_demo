package com.chen.boot.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Forget_chen
 * @className DruidConfig
 * @desc
 * @date 2022/6/10 16:22
 */
@Configuration
public class DruidConfig {

    //绑定application.yaml里的配置
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druidDataSource(){
        return new DruidDataSource();
    }

}
