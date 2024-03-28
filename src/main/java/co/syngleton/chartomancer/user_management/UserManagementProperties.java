package co.syngleton.chartomancer.user_management;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "usermanagement")
record UserManagementProperties(
        @DefaultValue("IN_MEMORY") RepositoryType repositoryType
) {

    enum RepositoryType {
        UNKNOWN,
        IN_MEMORY,
        IN_MEMORY_WITH_DEBUG_LOGGING,
        MONGO_DB
    }
}
