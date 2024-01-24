package co.syngleton.chartomancer.external_api_requesting;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.naming.ConfigurationException;


@Configuration
@AllArgsConstructor
class ExternalRequestingConfig {
    private final RequestingServiceFactory requestingServiceFactory;

    @Bean
    DataRequestingService dataRequestingService() throws ConfigurationException {
        return requestingServiceFactory.getDataRequestingService();
    }

}
