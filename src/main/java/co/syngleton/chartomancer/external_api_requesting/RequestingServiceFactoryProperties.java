package co.syngleton.chartomancer.external_api_requesting;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "requestingservicefactory", ignoreUnknownFields = false)
@Getter
@Setter
public class RequestingServiceFactoryProperties {
    private ExternalDataSource externalDataSource = ExternalDataSource.UNKNOWN;
    private String apiKey;
    private boolean freeSubscription = true;

    enum ExternalDataSource {
        UNKNOWN,
        CRYPTO_COMPARE
    }
}
