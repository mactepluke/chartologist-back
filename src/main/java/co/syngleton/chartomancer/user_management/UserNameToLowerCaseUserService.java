package co.syngleton.chartomancer.user_management;

import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
final class UserNameToLowerCaseUserService implements UserService {
    private final UserService userService;
    private static final String INVALID_USERNAME = "Username cannot be null";

    @Override
    public User create(String username, String password) {
        Objects.requireNonNull(username, INVALID_USERNAME);
        return userService.create(username.toLowerCase(), password);
    }

    @Override
    public User find(String username) {
        Objects.requireNonNull(username, INVALID_USERNAME);
        return userService.find(username.toLowerCase());
    }

    @Override
    public User update(String username, User updatedUser) {
        Objects.requireNonNull(username, INVALID_USERNAME);
        Objects.requireNonNull(updatedUser, INVALID_USERNAME);

        if (updatedUser.getUsername() != null) {
            updatedUser.setUsername(updatedUser.getUsername().toLowerCase());
        }
        return userService.update(username.toLowerCase(), updatedUser);
    }

    @Override
    public void delete(String username) {
        Objects.requireNonNull(username, INVALID_USERNAME);
        userService.delete(username.toLowerCase());
    }
}
