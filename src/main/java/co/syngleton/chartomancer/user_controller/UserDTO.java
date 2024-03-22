package co.syngleton.chartomancer.user_controller;

import co.syngleton.chartomancer.user_management.User;

record UserDTO(
    String username,
    String email,
    boolean enableLightMode
) {

    static UserDTO from(User user)  {
        return new UserDTO(user.getUsername(),
                user.getEmail(),
                user.getSettings().isEnableLightMode());
    }
}
