package co.syngleton.chartomancer.user_management;

import lombok.*;

import java.io.Serializable;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UserSettings implements Serializable {
    private boolean enableLightMode;
    private String email;
}
