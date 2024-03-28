package co.syngleton.chartomancer.user_management;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Log4j2
final class OnMongoDBUserRepositoryCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();

        String repositoryType = environment.getProperty("usermanagement.repository_type");

        return (repositoryType != null
                && repositoryType.equals(UserManagementProperties.RepositoryType.MONGO_DB.name()));
    }
}
