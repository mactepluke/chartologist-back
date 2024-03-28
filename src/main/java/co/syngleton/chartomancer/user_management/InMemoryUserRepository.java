package co.syngleton.chartomancer.user_management;

import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Log4j2
class InMemoryUserRepository implements UserRepository {
    protected final Map<String, User> users = new HashMap<>();

    @Override
    public User create(User user) {

        checkIsValid(user);
        user.setId(user.getUsername());
        users.put(user.getId(), user);

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
        if (read(user.getId()) == null) {
            return null;
        }
        users.put(user.getId(), user);

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
