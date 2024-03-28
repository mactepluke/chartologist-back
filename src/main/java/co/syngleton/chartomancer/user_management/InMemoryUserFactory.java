package co.syngleton.chartomancer.user_management;

final class InMemoryUserFactory extends AbstractUserFactory {

    @Override
    public User create(String username, String password, String hiddenPassword) {
        return new InMemoryUser(username, password, hiddenPassword);
    }

    @Override
    public User from(UserDTO userDTO) {
        User user = new InMemoryUser(userDTO.username(), userDTO.password(), "*".repeat(userDTO.password().length()));

        user.setSettings(UserSettings.builder()
                .email(userDTO.email())
                .enableLightMode(userDTO.enableLightMode())
                .build());
        return user;
    }
}
