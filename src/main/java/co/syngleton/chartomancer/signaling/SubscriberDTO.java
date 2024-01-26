package co.syngleton.chartomancer.signaling;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class SubscriberDTO {
    private String address;
    private boolean enabled;
}
