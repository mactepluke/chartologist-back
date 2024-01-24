package co.syngleton.chartomancer.security;

import co.syngleton.chartomancer.data.DataConfigTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = DataConfigTest.class)
@ActiveProfiles("test")
class ChartomancerApplicationTests {

    @Test
    @DisplayName("[UNIT] Spring context loads")
    void contextLoads() {
    }

}
