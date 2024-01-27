package co.syngleton.chartomancer.signaling;

import co.syngleton.chartomancer.charting_types.Timeframe;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Set;

@ConfigurationProperties(prefix = "signaling")
record SignalingProperties(
        @DefaultValue("false") boolean enabled,
        @DefaultValue("[]") Set<String> subscribersAddresses,
        @DefaultValue("100") double exampleAccountBalance,
        @DefaultValue("FOUR_HOUR") Set<Timeframe> rates
) {
}

