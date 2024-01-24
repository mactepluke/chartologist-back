package co.syngleton.chartomancer.charting;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "charting")
@Getter
@Setter
public class ChartingProperties {
    private HistoricalDataSource dataSource = HistoricalDataSource.UNKNOWN;
    private CsvReader csvReader = CsvReader.UNKNOWN;

    enum HistoricalDataSource {
        UNKNOWN,
        CSV_FILES
    }

    enum CsvReader {
        UNKNOWN,
        CRYPTO_DATA_DOWNLOAD
    }
}
