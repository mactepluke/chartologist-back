package co.syngleton.chartomancer.user_management;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.ConfigurationException;
import java.util.Objects;

@Configuration
@AllArgsConstructor
class UserManagementConfig {
    private final UserManagementProperties userManagementProperties;
    private final PasswordEncoder passwordEncoder;


    @Bean
    @Conditional(OnMongoDBUserRepositoryCondition.class)
    MongoDBUserRepository mongoDBUserRepository(MongoDBUserRepository mongoRepository) {
        return mongoRepository;
    }

    @Bean
    UserRepository userRepository(MongoDBUserRepository mongoDBUserRepository) throws ConfigurationException {
        return switch (userManagementProperties.repositoryType()) {
            case IN_MEMORY -> new InMemoryUserRepository();
            case MONGO_DB -> getMongoDBUserRepository(mongoDBUserRepository);
            case UNKNOWN -> throw new ConfigurationException("Unknown user repository type: " + userManagementProperties.repositoryType());
        };
    }

    private UserRepository getMongoDBUserRepository(MongoDBUserRepository mongoDBUserRepository) {
        Objects.requireNonNull(mongoDBUserRepository);
        return new MongoDBUserRepositoryAdapter(mongoDBUserRepository);
    }

    @Bean
    UserFactory userFactory() throws ConfigurationException {
        return switch (userManagementProperties.repositoryType()) {
            case IN_MEMORY -> new InMemoryUserFactory();
            case MONGO_DB -> new MongoDBUserFactory();
            case UNKNOWN -> throw new ConfigurationException("Unknown user repository type: " + userManagementProperties.repositoryType());
        };
    }

    @Bean
    UserService userService(UserRepository userRepository, UserFactory userFactory) {
        return new UserNameToLowerCaseUserService(new DefaultUserService(userRepository, passwordEncoder, userFactory));
    }
}
