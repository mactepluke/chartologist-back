package co.syngleton.chartomancer.user_controller;

import co.syngleton.chartomancer.user_management.UserSettings;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.userdetails.UserDetails;

record UserDTO(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&]).{8,30}$",
                message = "password must be 8-30 chars, at least 1 lower case, 1 upper case and 1 special char")
        @Size(min = 8, max = 30) String email,
        boolean enableLightMode
) {

    static UserDTO from(UserDetails user, UserSettings userSettings) {
        return new UserDTO(
                user.getUsername(),
                userSettings.getEmail(),
                userSettings.isEnableLightMode()
        );
    }
}
