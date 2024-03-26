package co.syngleton.chartomancer.user_management;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.ConfigurationException;
import java.util.Objects;

@Configuration
@AllArgsConstructor
class UserManagementConfig {
    private final UserManagementProperties userManagementProperties;
    private final MongoDBUserRepositoryAdapter mongoDBUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    UserRepository userRepository() throws ConfigurationException {
        return switch (userManagementProperties.repositoryType()) {
            case IN_MEMORY -> getInMemoryUserRepository();
            case MONGO_DB -> getMongoDBUserRepository();
            case UNKNOWN -> throw new ConfigurationException("Unknown user repository type: " + userManagementProperties.repositoryType());
        };
    }

    private UserRepository getInMemoryUserRepository() {
        return new InMemoryUserRepository();
    }

    private UserRepository getMongoDBUserRepository() {
        Objects.requireNonNull(mongoDBUserRepository, "MongoDBUserRepository is required for use the MongoDB repository type");
        return mongoDBUserRepository;
    }

    @Bean
    UserService userService() throws ConfigurationException {
        return new UserNameToLowerCaseUserService(new DefaultUserService(userRepository(), passwordEncoder));
    }


}
