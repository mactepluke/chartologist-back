package co.syngleton.chartomancer.user_management;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@AllArgsConstructor
@Log4j2
class DefaultUserService implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String INVALID_USER = "User cannot be null";
    private static final String INVALID_USERNAME = "Username cannot be null";
    private static final String INVALID_PASSWORD = "Password cannot be null";

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public User create(String username, String password) {

        Objects.requireNonNull(username, INVALID_USERNAME);
        Objects.requireNonNull(password, INVALID_PASSWORD);

        if (userRepository.read(username) != null) {
            log.error("User exists already: {}. Could not create.", username);
            return null;
        }
        User user = User.getNew(username, passwordEncoder.encode(password));

        return userRepository.create(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User find(String username) {

        Objects.requireNonNull(username, INVALID_USERNAME);

        return userRepository.read(username);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public User update(String username, User userToUpdate) {

        Objects.requireNonNull(username, INVALID_USERNAME);
        Objects.requireNonNull(userToUpdate, INVALID_USER);

        User user = userRepository.read(username);

        if (user == null) {
            log.error("User not found: {}. Could not update.", username);
            return null;
        }

        if (userIsInvalid(userToUpdate)) {
            log.error("Invalid user: {}. Could not update.", userToUpdate);
            return null;
        }

        userToUpdate.setId(user.getId());

        userToUpdate.setPassword(passwordEncoder.encode(userToUpdate.getPassword()));

        return userRepository.update(user);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void delete(String username) {
        Objects.requireNonNull(username, INVALID_USERNAME);
        userRepository.delete(username);
    }

    private boolean userIsInvalid(User user) {
        return user.getUsername() == null ||
                user.getPassword() == null ||
                user.getUsername().isBlank() ||
                user.getPassword().isBlank();
    }
}
