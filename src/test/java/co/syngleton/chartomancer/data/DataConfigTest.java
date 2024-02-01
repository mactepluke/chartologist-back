package co.syngleton.chartomancer.data;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DataConfigTest {

    @Bean
    MockData mockData() {
        return new MockData();
    }
}
