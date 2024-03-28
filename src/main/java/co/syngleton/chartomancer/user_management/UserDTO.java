package co.syngleton.chartomancer.user_management;

import jakarta.validation.constraints.Pattern;

import static co.syngleton.chartomancer.user_management.UserValidationConstants.*;

public record UserDTO(
        @Pattern(regexp = USERNAME_PATTERN, message = EMAIL_MESSAGE)
        String username,
        String password,
        @Pattern(regexp = EMAIL_PATTERN, message = EMAIL_MESSAGE)
        String email,
        boolean enableLightMode
) {

}
