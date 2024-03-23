package co.syngleton.chartomancer.security;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static co.syngleton.chartomancer.user_management.UserValidationConstants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    @Pattern(regexp = USERNAME_PATTERN, message = USERNAME_MESSAGE)
    private String username;
    @Pattern(regexp = PASSWORD_PATTERN, message = PASSWORD_MESSAGE)
    private String password;
}
