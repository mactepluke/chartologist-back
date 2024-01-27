package co.syngleton.chartomancer.external_api_requesting;

import co.syngleton.chartomancer.charting.GraphUpscaler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.naming.ConfigurationException;


@Component
@AllArgsConstructor
final class DefaultRequestingServiceFactory implements RequestingServiceFactory {

    private final GraphUpscaler graphUpscaler;
    private final CryptoCompareApiProxy cryptoCompareApiProxy;
    private final RequestingServiceFactoryProperties rsfp;

    @Override
    public DataRequestingService getDataRequestingService() throws ConfigurationException {
        return switch (rsfp.externalDataSource()) {
            case CRYPTO_COMPARE -> getCryptoCompareRequestingService();
            case UNKNOWN ->
                    throw new ConfigurationException("Unknown external data source: " + rsfp.externalDataSource());
        };
    }

    private DataRequestingService getCryptoCompareRequestingService() {
        return new CryptoCompareRequestingService(cryptoCompareApiProxy, graphUpscaler, rsfp.apiKey(), rsfp.freeSubscription());
    }

}
