package co.syngleton.chartomancer.charting;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "charting")
record ChartingProperties(
        @DefaultValue("UNKNOWN") HistoricalDataSource dataSource,
        @DefaultValue("UNKNOWN") CsvReader csvReader,
        @DefaultValue("true") boolean repairMissingCandles
) {

    enum HistoricalDataSource {
        UNKNOWN,
        CSV_FILES
    }

    enum CsvReader {
        UNKNOWN,
        CRYPTO_DATA_DOWNLOAD
    }
}
