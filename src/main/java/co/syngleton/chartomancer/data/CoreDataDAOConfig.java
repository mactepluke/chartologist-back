package co.syngleton.chartomancer.data;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.naming.ConfigurationException;

@Configuration
@AllArgsConstructor
class CoreDataDAOConfig {
    private final DataProperties dataProperties;

    @Bean
    CoreDataDAO coreDataDAO() throws ConfigurationException {

        return switch (dataProperties.source()) {
            case SERIALIZED -> getSerializedCoreDataDAO();
            case UNKNOWN -> throw new ConfigurationException("Unknown core data source: " + dataProperties.source());
        };
    }

    private CoreDataDAO getSerializedCoreDataDAO() {
        return new SerializedCoreDataDAO();
    }

}
