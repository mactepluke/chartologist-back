package co.syngleton.chartomancer.charting;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "charting")
@Getter
@Setter
class ChartingProperties {
    private HistoricalDataSource dataSource = HistoricalDataSource.UNKNOWN;
    private CsvReader csvReader = CsvReader.UNKNOWN;
    private boolean repairMissingCandles = false;

    enum HistoricalDataSource {
        UNKNOWN,
        CSV_FILES
    }

    enum CsvReader {
        UNKNOWN,
        CRYPTO_DATA_DOWNLOAD
    }
}
