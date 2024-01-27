package co.syngleton.chartomancer.charting;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.naming.ConfigurationException;

@Configuration
@AllArgsConstructor
class ChartingConfig {
    private final ChartingProperties chartingProperties;

    @Bean
    HistoricalDataDAO historicalDataDAO() throws ConfigurationException {
        return switch (chartingProperties.dataSource()) {
            case CSV_FILES -> getHistoricalDataDAOCsvReader();
            case UNKNOWN ->
                    throw new ConfigurationException("Unknown historical data source: " + chartingProperties.dataSource());
        };
    }

    private HistoricalDataCsvReader getHistoricalDataDAOCsvReader() throws ConfigurationException {
        return switch (chartingProperties.csvReader()) {
            case CRYPTO_DATA_DOWNLOAD -> getCryptoDataDownloadCsvReader();
            case UNKNOWN -> throw new ConfigurationException("Unknown CSV reader: " + chartingProperties.csvReader());
        };
    }

    private HistoricalDataCsvReader getCryptoDataDownloadCsvReader() {
        return new CryptoDataDownloadCsvReader();
    }
}
