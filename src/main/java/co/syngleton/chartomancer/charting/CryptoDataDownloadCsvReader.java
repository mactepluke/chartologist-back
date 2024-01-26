package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.charting_types.Symbol;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
final class CryptoDataDownloadCsvReader extends HistoricalDataCsvReader {
    private static final String CRYPTO_DATA_DOWNLOAD_USD_BTC = "unix,date,symbol,open,high,low,close,Volume USD,Volume BTC";
    private static final String CRYPTO_DATA_DOWNLOAD_USD_ETH = "unix,date,symbol,open,high,low,close,Volume USD,Volume ETH";
    private static final String CRYPTO_DATA_DOWNLOAD_BTC_USD = "unix,date,symbol,open,high,low,close,Volume BTC,Volume USD";
    private static final String CRYPTO_DATA_DOWNLOAD_ETH_USD = "unix,date,symbol,open,high,low,close,Volume ETH,Volume USD";

    private final Map<String, ColumnProperties> formatHeaders = new HashMap<>();

    @Override
    protected Map<String, ColumnProperties> getFormatHeaders() {

        if (!formatHeaders.isEmpty()) {
            return formatHeaders;
        }

        formatHeaders.put(CRYPTO_DATA_DOWNLOAD_USD_BTC, new ColumnProperties(0, 2, 3, 4, 5, 6, 7, Symbol.BTC_USD));
        formatHeaders.put(CRYPTO_DATA_DOWNLOAD_USD_ETH, new ColumnProperties(0, 2, 3, 4, 5, 6, 7, Symbol.ETH_USD));
        formatHeaders.put(CRYPTO_DATA_DOWNLOAD_BTC_USD, new ColumnProperties(0, 2, 3, 4, 5, 6, 8, Symbol.BTC_USD));
        formatHeaders.put(CRYPTO_DATA_DOWNLOAD_ETH_USD, new ColumnProperties(0, 2, 3, 4, 5, 6, 8, Symbol.ETH_USD));

        return formatHeaders;
    }

}
