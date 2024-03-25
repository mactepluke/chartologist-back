package co.syngleton.chartomancer.user_controller;

import co.syngleton.chartomancer.user_management.User;
import co.syngleton.chartomancer.user_management.UserSettings;
import jakarta.validation.constraints.Pattern;

import static co.syngleton.chartomancer.user_management.UserValidationConstants.*;

record UserDTO(
        @Pattern(regexp = USERNAME_PATTERN, message = EMAIL_MESSAGE)
        String username,
        String password,
        @Pattern(regexp = EMAIL_PATTERN, message = EMAIL_MESSAGE)
        String email,
        boolean enableLightMode
) {

    static UserDTO fromEntity(User user) {
        return new UserDTO(
                user.getUsername(),
                user.getHiddenPassword(),
                user.getEmail(),
                user.hasEnableLightMode()
        );
    }

    static User toEntity(UserDTO userDTO) {
        User user = User.getNew(userDTO.username(), userDTO.password(), "*".repeat(userDTO.password().length()));

        user.setSettings(UserSettings.builder()
                .email(userDTO.email())
                .enableLightMode(userDTO.enableLightMode())
                .build());
        return user;
    }


}
