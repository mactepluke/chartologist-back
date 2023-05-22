package com.syngleton.chartomancy;

import com.syngleton.chartomancy.configuration.DataConfigTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = DataConfigTest.class)
class ChartomancyApplicationTests {

    @Test
    @DisplayName("[UNIT] Spring context loads")
    void contextLoads() {
    }

}
