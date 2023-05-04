package com.syngleton.chartomancy.analytics;

import com.syngleton.chartomancy.configuration.DataConfigTest;
import com.syngleton.chartomancy.factory.CandleFactory;
import com.syngleton.chartomancy.model.charting.candles.FloatCandle;
import com.syngleton.chartomancy.model.charting.candles.IntCandle;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
class AnalyserTests {

    private static final int GRANULARITY = 100;
    private LocalDateTime candleDate;

    @Autowired
    Analyzer analyzer;
    @Autowired
    CandleFactory candleFactory;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING ANALYZER TESTS ***");

        candleDate = LocalDateTime.now();
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING ANALYZER TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Calculates match score")

    void calculateMatchScoreTest()   {

        List<FloatCandle> floatCandles;
        List<IntCandle> intCandles;
        List<IntCandle> intCandlesToMatch1 = new ArrayList<>();
        List<IntCandle> intCandlesToMatch2 = new ArrayList<>();

        FloatCandle floatCandle1 = new FloatCandle(candleDate, 20, 100, 0, 80, 20);
        FloatCandle floatCandle2 = new FloatCandle(candleDate, 20, 90, 10, 80, 20);
        FloatCandle floatCandle3 = new FloatCandle(candleDate, 40, 100, 40, 60, 20);

        floatCandles = new ArrayList<>(List.of(floatCandle1, floatCandle2, floatCandle3));
        intCandles = candleFactory.streamlineToIntCandles(floatCandles, GRANULARITY);

        intCandlesToMatch1.add(intCandles.get(1));
        intCandlesToMatch2.add(intCandles.get(2));

        log.debug(intCandlesToMatch1);
        log.debug(intCandlesToMatch2);

        assertEquals(38, analyzer.calculateMatchScoreWithExponentialSmoothing(intCandlesToMatch1, intCandlesToMatch2));
    }
}
