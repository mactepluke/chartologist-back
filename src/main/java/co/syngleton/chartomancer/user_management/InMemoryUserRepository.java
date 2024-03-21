package co.syngleton.chartomancer.user_management;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

final class InMemoryUserRepository implements UserRepository {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, User> users = new HashMap<>();

    @Override
    public User create(User user) {

        checkIsValid(user);

        user.setId(passwordEncoder.encode(user.getUsername()));

        users.put(user.getUsername(), user);

        return user;
    }

    @Override
    public User read(String username) {
        Objects.requireNonNull(username);
        return users.get(username);
    }

    @Override
    public User update(User user) {

        checkIsValid(user);

        if (read(user.getUsername()) == null) {
            return null;
        }

        users.put(user.getUsername(), user);

        return user;
    }

    @Override
    public void delete(String username) {
        Objects.requireNonNull(username);
        users.remove(username);
    }

    private void checkIsValid(User user) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(user.getUsername());
        Objects.requireNonNull(user.getPassword());
    }
}
