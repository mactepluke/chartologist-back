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
    private Analyzer analyzer;

    @Autowired
    CandleFactory candleFactory;

    private List<IntCandle> intCandles;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING ANALYZER TESTS ***");
        LocalDateTime candleDate = LocalDateTime.now();
        List<FloatCandle> floatCandles;

        FloatCandle floatCandle1 = new FloatCandle(candleDate, 20, 100, 0, 80, 20);
        FloatCandle floatCandle2 = new FloatCandle(candleDate, 20, 90, 10, 80, 20);
        FloatCandle floatCandle3 = new FloatCandle(candleDate, 40, 100, 40, 60, 20);

        floatCandles = new ArrayList<>(List.of(floatCandle1, floatCandle2, floatCandle3));
        intCandles = candleFactory.streamlineToIntCandles(floatCandles, GRANULARITY);
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING ANALYZER TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Calculates match score")
    void calculateMatchScoreBasicTest()   {

        List<IntCandle> intCandlesToMatch1 = new ArrayList<>(List.of(intCandles.get(1)));
        List<IntCandle> intCandlesToMatch2 = new ArrayList<>(List.of(intCandles.get(2)));

        analyzer = new Analyzer(Smoothing.LINEAR, 0, 0, false, false);

        assertEquals(38, analyzer.calculateMatchScore(intCandlesToMatch1, intCandlesToMatch2));
    }

    @Test
    @DisplayName("[UNIT] Calculates match score")
    void calculateMatchScoreExtrapolatedTest()   {

        List<IntCandle> intCandlesToMatch1 = new ArrayList<>(List.of(intCandles.get(1)));
        List<IntCandle> intCandlesToMatch2 = new ArrayList<>(List.of(intCandles.get(2)));

        analyzer = new Analyzer(Smoothing.LINEAR, 0, 0, false, true);

        assertEquals(52, analyzer.calculateMatchScore(intCandlesToMatch1, intCandlesToMatch2));
    }
}