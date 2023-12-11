package co.syngleton.chartomancer.configuration;

import co.syngleton.chartomancer.analytics.data.CoreData;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DataConfigTest {

    @Bean
    CoreData coreData() {
        return new CoreData();
    }

    @Bean
    MockData mockData() {
        return new MockData();
    }
}
