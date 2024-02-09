package co.syngleton.chartomancer.configuration;

import co.syngleton.chartomancer.charting.CandleRescaler;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class GlobalTestConfig {

    @Bean
    CandleRescaler candleRescaler() {
        return new MockCandleRescaler();
    }
}
