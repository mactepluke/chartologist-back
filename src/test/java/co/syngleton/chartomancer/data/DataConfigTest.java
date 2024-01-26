package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.shared_domain.CoreData;
import co.syngleton.chartomancer.shared_domain.DefaultCoreData;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DataConfigTest {

    @Bean
    CoreData coreData() {
        return new DefaultCoreData();
    }

    @Bean
    MockData mockData() {
        return new MockData();
    }
}