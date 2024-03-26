package co.syngleton.chartomancer.user_management;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static co.syngleton.chartomancer.user_management.UserValidationConstants.PASSWORD_PATTERN;

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
    public User create(final String username, final String password) {

        Objects.requireNonNull(username, INVALID_USERNAME);
        Objects.requireNonNull(password, INVALID_PASSWORD);

        if (userRepository.read(username) != null) {
            log.error("User exists already: {}. Could not create.", username);
            return null;
        }
        final User user = User.getNew(username, passwordEncoder.encode(password), "*".repeat(password.length()));

        return userRepository.create(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User find(final String username) {

        Objects.requireNonNull(username, INVALID_USERNAME);

        return userRepository.read(username);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public User update(final String username, final User userToUpdate) {

        Objects.requireNonNull(username, INVALID_USERNAME);
        Objects.requireNonNull(userToUpdate, INVALID_USER);

        final User user = userRepository.read(username);

        if (user == null) {
            log.error("User not found: {}. Could not update.", username);
            return null;
        }

        if (userIsInvalid(userToUpdate)) {
            log.error("Invalid user: {}. Could not update.", userToUpdate);
            return null;
        }

        if (userRepository.read(userToUpdate.getUsername()) != null && !userToUpdate.getUsername().equals(user.getUsername())) {
            log.error("User with the new username exists already: {}. Could not update.", userToUpdate.getUsername());
            return null;
        }

        userToUpdate.setId(user.getId());

        if (passwordIsInvalid(userToUpdate)) {
            log.error("Invalid new password: {}. Could not update.", userToUpdate);
            return null;
        }
        userToUpdate.setPassword(passwordEncoder.encode(userToUpdate.getPassword()));

        return userRepository.update(userToUpdate);
    }

    private boolean passwordIsInvalid(User user) {
        return !user.getPassword().matches(PASSWORD_PATTERN);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void delete(final String username) {
        Objects.requireNonNull(username, INVALID_USERNAME);
        userRepository.delete(username);
    }

    private boolean userIsInvalid(final User user) {
        return user.getUsername() == null ||
                user.getPassword() == null ||
                user.getUsername().isBlank() ||
                user.getPassword().isBlank();
    }
}
