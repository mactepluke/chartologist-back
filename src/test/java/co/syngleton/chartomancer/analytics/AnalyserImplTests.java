package co.syngleton.chartomancer.analytics;

import co.syngleton.chartomancer.charting.CandleRescaler;
import co.syngleton.chartomancer.core_entities.FloatCandle;
import co.syngleton.chartomancer.core_entities.IntCandle;
import co.syngleton.chartomancer.data.DataConfigTest;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
@ActiveProfiles("test")
class AnalyserImplTests {

    private static final int GRANULARITY = 100;
    @Autowired
    CandleRescaler candleRescaler;
    private AnalyzerImpl defaultAnalyzer;
    private List<IntCandle> intCandles;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING ANALYZER TESTS ***");
        LocalDateTime candleDate = LocalDateTime.now();
        List<FloatCandle> floatCandles;

        FloatCandle floatCandle1 = new FloatCandle(candleDate, 20, 100, 0, 80, 20);
        FloatCandle floatCandle2 = new FloatCandle(candleDate, 20, 90, 10, 80, 20);
        FloatCandle floatCandle3 = new FloatCandle(candleDate, 40, 100, 40, 60, 20);
        FloatCandle floatCandle4 = new FloatCandle(candleDate, 50, 60, 20, 40, 30);

        floatCandles = new ArrayList<>(List.of(floatCandle1, floatCandle2, floatCandle3, floatCandle4));
        intCandles = candleRescaler.rescale(floatCandles, GRANULARITY);

        this.defaultAnalyzer = new AnalyzerImpl(Smoothing.NONE, 0, 0, false, false);
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING ANALYZER TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Calculates overlap amount between integer intervals")
    void calculateOverlapAmountTest() {
        assertEquals(20, defaultAnalyzer.overlapAmount(20, 80, 40, 60));
        assertEquals(20, defaultAnalyzer.overlapAmount(20, 80, 60, 40));
        assertEquals(10, defaultAnalyzer.overlapAmount(-20, 30, 20, 120));
        assertEquals(15, defaultAnalyzer.overlapAmount(-50, -90, 40, -65));
        assertEquals(0, defaultAnalyzer.overlapAmount(0, 0, -10, 10));
        assertEquals(0, defaultAnalyzer.overlapAmount(50, -90, 0, 0));
        assertEquals(0, defaultAnalyzer.overlapAmount(10, 0, 0, -65));
        assertEquals(5, defaultAnalyzer.overlapAmount(10, 0, 5, 55));
        assertEquals(0, defaultAnalyzer.overlapAmount(0, 10, 10, 20));
        assertEquals(0, defaultAnalyzer.overlapAmount(0, 10, 15, 20));
        assertEquals(0, defaultAnalyzer.overlapAmount(100, 40, 30, -10));
    }


    @Test
    @DisplayName("[UNIT] Calculates basic match score")
    void calculateMatchScoreBasicTest() {

        List<IntCandle> intCandlesToMatch1 = new ArrayList<>(List.of(intCandles.get(1)));
        List<IntCandle> intCandlesToMatch2 = new ArrayList<>(List.of(intCandles.get(2)));

        AnalyzerImpl localAnalyzer = new AnalyzerImpl(Smoothing.NONE, 0, 0, false, false);

        assertEquals(38, localAnalyzer.calculateMatchScore(intCandlesToMatch1, intCandlesToMatch2));
    }

    @Test
    @DisplayName("[UNIT] Calculates extrapolated match score")
    void calculateMatchScoreExtrapolatedTest() {

        List<IntCandle> intCandlesToMatch1 = new ArrayList<>(List.of(intCandles.get(1)));
        List<IntCandle> intCandlesToMatch2 = new ArrayList<>(List.of(intCandles.get(2)));

        AnalyzerImpl localAnalyzer = new AnalyzerImpl(Smoothing.LINEAR, 0, 0, false, true);

        assertEquals(52, localAnalyzer.calculateMatchScore(intCandlesToMatch1, intCandlesToMatch2));
    }

    @Test
    @DisplayName("[UNIT] Calculates basic match score with different smoothing values")
    void calculateMatchScoreBasicWithSmoothingVariationsTest() {

        List<IntCandle> intCandlesToMatch1 = new ArrayList<>(List.of(intCandles.get(0), intCandles.get(1)));
        List<IntCandle> intCandlesToMatch2 = new ArrayList<>(List.of(intCandles.get(2), intCandles.get(3)));

        AnalyzerImpl localAnalyzer = new AnalyzerImpl(Smoothing.NONE, 0, 0, false, false);
        int noSmoothingMatchScore = localAnalyzer.calculateMatchScore(intCandlesToMatch1, intCandlesToMatch2);

        localAnalyzer = new AnalyzerImpl(Smoothing.LINEAR, 0, 0, false, false);
        int linearSmoothingMatchScore = localAnalyzer.calculateMatchScore(intCandlesToMatch1, intCandlesToMatch2);

        localAnalyzer = new AnalyzerImpl(Smoothing.EXPONENTIAL, 0, 0, false, false);
        int exponentialSmoothingMatchScore = localAnalyzer.calculateMatchScore(intCandlesToMatch1, intCandlesToMatch2);


        for (int i = 0; i < 10; i++) {
            log.debug(Math.exp(i));
        }
        log.debug("No smoothing match score: " + noSmoothingMatchScore);
        log.debug("Linear smoothing match score: " + linearSmoothingMatchScore);
        log.debug("Exponential smoothing match score: " + exponentialSmoothingMatchScore);
        log.debug(noSmoothingMatchScore < linearSmoothingMatchScore && linearSmoothingMatchScore < exponentialSmoothingMatchScore);

        assertEquals(10, noSmoothingMatchScore);
        assertEquals(15, linearSmoothingMatchScore);
        assertEquals(22, exponentialSmoothingMatchScore);
        assertTrue(noSmoothingMatchScore < linearSmoothingMatchScore && linearSmoothingMatchScore < exponentialSmoothingMatchScore);
    }

    @Test
    @DisplayName("[UNIT] Filters price variation")
    void filterPriceVariationTest() {

        AnalyzerImpl localAnalyzer = new AnalyzerImpl(Smoothing.NONE, 0, 5, false, false);
        assertEquals(0, localAnalyzer.filterPriceVariation(4));

        localAnalyzer = new AnalyzerImpl(Smoothing.NONE, 0, 5, true, false);
        assertEquals(-5.25f, localAnalyzer.filterPriceVariation(-5));
    }

}
