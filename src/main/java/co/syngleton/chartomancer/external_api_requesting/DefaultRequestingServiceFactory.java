package co.syngleton.chartomancer.external_api_requesting;

import co.syngleton.chartomancer.charting.GraphUpscaler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.ConfigurationException;

@Component
final class DefaultRequestingServiceFactory implements RequestingServiceFactory {

    private final GraphUpscaler graphUpscaler;
    private final CryptoCompareApiProxy cryptoCompareApiProxy;
    @Value("${external_data_source:UNKNOWN}")
    ExternalDataSource externalDataSource;
    @Value("${api_key}")
    private String apiKey;
    @Value("${free_subscription:true}")
    private boolean freeSubscription;

    @Autowired
    DefaultRequestingServiceFactory(CryptoCompareApiProxy cryptoCompareApiProxy,
                                    GraphUpscaler graphUpscaler) {
        this.cryptoCompareApiProxy = cryptoCompareApiProxy;
        this.graphUpscaler = graphUpscaler;
    }

    @Override
    public DataRequestingService getDataRequestingService() throws ConfigurationException {
        return switch (externalDataSource) {
            case CRYPTO_COMPARE -> getCryptoCompareRequestingService();
            case UNKNOWN -> throw new ConfigurationException("Unknown external data source: " + externalDataSource);
        };
    }

    private DataRequestingService getCryptoCompareRequestingService() {
        return new CryptoCompareRequestingService(cryptoCompareApiProxy, graphUpscaler, apiKey, freeSubscription);
    }

    enum ExternalDataSource {
        UNKNOWN,
        CRYPTO_COMPARE
    }

}
