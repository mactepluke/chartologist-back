package co.syngleton.chartomancer.service;

import co.syngleton.chartomancer.analytics.model.Graph;
import co.syngleton.chartomancer.analytics.model.Symbol;
import co.syngleton.chartomancer.analytics.model.Timeframe;
import co.syngleton.chartomancer.analytics.service.CoreDataService;
import co.syngleton.chartomancer.configuration.DataConfigTest;
import co.syngleton.chartomancer.signaling.service.datasource.ExternalDataSourceService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * All API request tests must be performed only sporadically to avoid account limit exhaustion,
 * and disabled the rest of the time.
 */
@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
@ActiveProfiles("test")
class CryptoCompareApiTests {

    @Autowired
    ExternalDataSourceService cryptoCompareService;
    @Autowired
    CoreDataService coreDataService;

    /*https://min-api.cryptocompare.com/data/v2/histohour?fsym=BTC&tsym=USD&limit=30&api_key=d65dc84a06302c8e5c992a22f9bcba743964c9d02e776270fe4251b564d47c25*/
    @Test
    @Disabled
    @DisplayName("[UNIT] Fetches a valid OHLCV DTO from the CryptoCompare API")
    void getLatestPriceHistoryGraphTest() {
        Symbol symbol = Symbol.BTC_USD;
        Timeframe timeframe = Timeframe.HOUR;
        int size = 30;

        Graph graph = cryptoCompareService.getLatestPriceHistoryGraph(symbol, timeframe, size);

        assertEquals(symbol, graph.getSymbol());
        assertEquals(timeframe, graph.getTimeframe());
        assertEquals(size, graph.getFloatCandles().size());

    }
}
