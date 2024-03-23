package co.syngleton.chartomancer.config_controller;

import static co.syngleton.chartomancer.user_management.UserValidationConstants.*;

record UserValidationDataDTO(
        String usernamePattern,
        String usernamePassword,
        String emailPattern,
        String usernameMessage,
        String passwordMessage,
        String emailMessage
) {
    static UserValidationDataDTO fetch() {
        return new UserValidationDataDTO(
                USERNAME_PATTERN,
                PASSWORD_PATTERN,
                EMAIL_PATTERN,
                USERNAME_MESSAGE,
                PASSWORD_MESSAGE,
                EMAIL_MESSAGE
        );
    }
}
