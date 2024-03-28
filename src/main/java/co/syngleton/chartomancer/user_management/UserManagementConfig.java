package co.syngleton.chartomancer.user_management;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.ConfigurationException;

@Configuration
@AllArgsConstructor
class UserManagementConfig {
    private final UserManagementProperties userManagementProperties;
    private final PasswordEncoder passwordEncoder;

    @Bean
    MongoDBUserRepository mongoDBUserRepository(MongoDBUserRepository mongoRepository) {
        return mongoRepository;
    }

    @Bean
    UserRepository userRepository(MongoDBUserRepository mongoRepository) throws ConfigurationException {
        return switch (userManagementProperties.repositoryType()) {
            case IN_MEMORY -> new InMemoryUserRepository();
            case IN_MEMORY_WITH_DEBUG_LOGGING -> new InMemoryUserRepositoryWithDebugLogging();
            case MONGO_DB -> getMongoDBUserRepository(mongoRepository);
            case UNKNOWN -> throw new ConfigurationException("Unknown user repository type: " + userManagementProperties.repositoryType());
        };
    }

    private UserRepository getMongoDBUserRepository(MongoDBUserRepository mongoRepository) {
        return new MongoDBUserRepositoryAdapter(mongoRepository);
    }

    @Bean
    UserFactory userFactory() throws ConfigurationException {
        return switch (userManagementProperties.repositoryType()) {
            case IN_MEMORY, IN_MEMORY_WITH_DEBUG_LOGGING -> new InMemoryUserFactory();
            case MONGO_DB -> new MongoDBUserFactory();
            case UNKNOWN -> throw new ConfigurationException("Unknown user repository type: " + userManagementProperties.repositoryType());
        };
    }

    @Bean
    UserService userService(UserRepository userRepository, UserFactory userFactory) {
        return new UserNameToLowerCaseUserService(new DefaultUserService(userRepository, passwordEncoder, userFactory));
    }
}
