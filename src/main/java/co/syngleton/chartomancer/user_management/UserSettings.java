package co.syngleton.chartomancer.user_management;

import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.io.Serializable;

import static co.syngleton.chartomancer.user_management.UserValidationConstants.EMAIL_MESSAGE;
import static co.syngleton.chartomancer.user_management.UserValidationConstants.EMAIL_PATTERN;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UserSettings implements Serializable {
    private boolean enableLightMode;
    @Pattern(regexp = EMAIL_PATTERN, message = EMAIL_MESSAGE)
    private String email;
}
