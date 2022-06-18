package com.chen.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EpidemicChinaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EpidemicChinaApplication.class, args);
    }

}
