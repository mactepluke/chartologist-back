package co.syngleton.chartomancer.user_management;

abstract class AbstractUserFactory implements UserFactory {

    @Override
    public UserDTO from(User user) {
        return new UserDTO(
                user.getUsername(),
                user.getHiddenPassword(),
                user.getEmail(),
                user.hasEnableLightMode()
        );
    }

}
