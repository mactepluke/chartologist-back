package com.syngleton.chartomancy;

import com.syngleton.chartomancy.data.AppData;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DataConfigTest {

    @Bean
    AppData appData() {
        return new AppData();
    }

}
