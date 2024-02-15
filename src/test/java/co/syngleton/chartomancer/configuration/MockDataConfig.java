package co.syngleton.chartomancer.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MockDataConfig {

    @Bean
    MockData mockData() {
        return new MockData();
    }
}
