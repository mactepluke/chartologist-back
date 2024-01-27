package co.syngleton.chartomancer.signaling;

import lombok.Data;

@Data
class SubscriberDTO {
    private String address;
    private boolean enabled;
}
