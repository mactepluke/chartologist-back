package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.configuration.DataConfigTest;
import com.syngleton.chartomancy.model.charting.misc.Graph;
import com.syngleton.chartomancy.model.charting.misc.Symbol;
import com.syngleton.chartomancy.model.charting.misc.Timeframe;
import com.syngleton.chartomancy.service.api.ExternalDataSourceService;
import com.syngleton.chartomancy.service.domain.DataService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
class CryptoCompareApiTests {

    @Autowired
    ExternalDataSourceService cryptoCompareService;
    @Autowired
    DataService dataService;

    /*https://min-api.cryptocompare.com/data/v2/histohour?fsym=BTC&tsym=USD&limit=30&api_key=d65dc84a06302c8e5c992a22f9bcba743964c9d02e776270fe4251b564d47c25*/
    @Test
    @Disabled
    @DisplayName("[UNIT] Fetches a valid OHLCV DTO from the CryptoCompare API")
    void getLatestPriceHistoryGraphTest() {
        Symbol symbol = Symbol.BTC_USD;
        Timeframe timeframe = Timeframe.HOUR;
        int size = 30;

        Graph graph = cryptoCompareService.getLatestPriceHistoryGraph(symbol, timeframe, size);

        dataService.printGraph(graph);

        assertEquals(symbol, graph.getSymbol());
        assertEquals(timeframe, graph.getTimeframe());
        assertEquals(size, graph.getFloatCandles().size());

    }
}
