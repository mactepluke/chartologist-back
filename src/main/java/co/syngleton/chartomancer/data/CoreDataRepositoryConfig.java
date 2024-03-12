package co.syngleton.chartomancer.data;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.naming.ConfigurationException;

@Configuration
@AllArgsConstructor
class CoreDataRepositoryConfig {
    private final DataProperties dataProperties;

    @Bean
    CoreDataRepository coreDataDAO() throws ConfigurationException {

        return switch (dataProperties.source()) {
            case SERIALIZED -> getSerializedCoreDataDAO();
            case UNKNOWN -> throw new ConfigurationException("Unknown core data source: " + dataProperties.source());
        };
    }

    private CoreDataRepository getSerializedCoreDataDAO() {
        return new SerializedCoreDataRepository();
    }

}
