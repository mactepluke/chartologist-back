package com.syngleton.chartomancy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ChartomancyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChartomancyApplication.class, args);
    }
}
