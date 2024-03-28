package co.syngleton.chartomancer.user_management;

final class MongoDBUserFactory extends AbstractUserFactory {

    @Override
    public User create(String username, String password, String hiddenPassword) {
        return new MongoDbUser(username, password, hiddenPassword);
    }

    @Override
    public User from(UserDTO userDTO) {
        User user = new MongoDbUser(userDTO.username(), userDTO.password(), "*".repeat(userDTO.password().length()));

        user.setSettings(UserSettings.builder()
                .email(userDTO.email())
                .enableLightMode(userDTO.enableLightMode())
                .build());
        return user;
    }
}
