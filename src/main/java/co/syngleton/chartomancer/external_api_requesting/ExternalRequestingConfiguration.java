package co.syngleton.chartomancer.external_api_requesting;

import co.syngleton.chartomancer.charting.GraphUpscaler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.naming.ConfigurationException;


@Configuration
class ExternalRequestingConfiguration {
    private final GraphUpscaler graphUpscaler;
    private final CryptoCompareApiProxy cryptoCompareApiProxy;
    @Value("${external_data_source}")
    ExternalDataSource externalDataSource;
    @Value("${api_key}")
    private String apiKey;
    @Value("${free_subscription:true}")
    private boolean freeSubscription;

    @Autowired
    ExternalRequestingConfiguration(CryptoCompareApiProxy cryptoCompareApiProxy,
                                    GraphUpscaler graphUpscaler) {
        this.cryptoCompareApiProxy = cryptoCompareApiProxy;
        this.graphUpscaler = graphUpscaler;
    }

    @Bean
    DataRequestingService dataRequestingService() throws ConfigurationException {

        return switch (externalDataSource) {
            case CRYPTO_COMPARE -> getCryptoCompareRequestingService();
            default -> throw new ConfigurationException("Unknown external data source: " + externalDataSource);
        };

    }

    private DataRequestingService getCryptoCompareRequestingService() {
        return new CryptoCompareRequestingService(cryptoCompareApiProxy, graphUpscaler, apiKey, freeSubscription);
    }

    enum ExternalDataSource {
        CRYPTO_COMPARE
    }


}
