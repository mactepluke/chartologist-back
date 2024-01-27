package co.syngleton.chartomancer.signaling;

import co.syngleton.chartomancer.charting_types.Timeframe;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "signaling")
@Data
class SignalingProperties {
    private boolean enabled = false;
    private Set<String> subscribersAddresses = new HashSet<>();
    private double exampleAccountBalance = 100;
    private Set<Timeframe> rates = new HashSet<>();
}
