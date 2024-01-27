package co.syngleton.chartomancer.external_api_requesting;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "requestingservicefactory")
record RequestingServiceFactoryProperties(
        @DefaultValue("UNKNOWN") ExternalDataSource externalDataSource,
        @DefaultValue("(Undefined API Key)") String apiKey,
        @DefaultValue("true") boolean freeSubscription
) {
    enum ExternalDataSource {
        UNKNOWN,
        CRYPTO_COMPARE
    }
}
