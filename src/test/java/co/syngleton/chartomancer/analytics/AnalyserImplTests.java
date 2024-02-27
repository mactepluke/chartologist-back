package co.syngleton.chartomancer.analytics;

import co.syngleton.chartomancer.charting.CandleRescaler;
import co.syngleton.chartomancer.configuration.MockConfig;
import co.syngleton.chartomancer.configuration.MockData;
import co.syngleton.chartomancer.configuration.MockDataConfig;
import co.syngleton.chartomancer.core_entities.IntCandle;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {MockConfig.class, MockDataConfig.class})
@ActiveProfiles("test")
class AnalyserImplTests {

    @Autowired
    CandleRescaler candleRescaler;
    @Autowired
    MockData mockData;

    private DefaultAnalyzer defaultAnalyzer;
    private List<IntCandle> intCandles;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING ANALYZER IMPL TESTS ***");
        LocalDateTime candleDate = LocalDateTime.now();
        this.intCandles = mockData.getIntCandles();

        this.defaultAnalyzer = new DefaultAnalyzer(Smoothing.NONE, 0, 0, false, false);

    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING ANALYZER IMPL TESTS ***");
    }


    @Test
    @DisplayName("[UNIT] Calculates the surface area of a candle")
    void calculateCandleSurfaceTest() {
        assertEquals(100, defaultAnalyzer.calculateCandleSurface(intCandles.get(0)));
        assertEquals(80, defaultAnalyzer.calculateCandleSurface(intCandles.get(1)));
        assertEquals(60, defaultAnalyzer.calculateCandleSurface(intCandles.get(2)));
        assertEquals(40, defaultAnalyzer.calculateCandleSurface(intCandles.get(3)));
    }

    @Test
    @DisplayName("[UNIT] Determines whether candles are of same color")
    void haveSameColorTest() {
        assertTrue(defaultAnalyzer.haveSameColor(intCandles.get(0), intCandles.get(1)));
        assertTrue(defaultAnalyzer.haveSameColor(intCandles.get(1), intCandles.get(2)));
        assertFalse(defaultAnalyzer.haveSameColor(intCandles.get(3), intCandles.get(0)));
        assertFalse(defaultAnalyzer.haveSameColor(intCandles.get(0), intCandles.get(3)));
        assertFalse(defaultAnalyzer.haveSameColor(intCandles.get(4), intCandles.get(0)));
        assertTrue(defaultAnalyzer.haveSameColor(intCandles.get(4), intCandles.get(4)));
        assertTrue(defaultAnalyzer.haveSameColor(intCandles.get(4), intCandles.get(4)));
        assertTrue(defaultAnalyzer.haveSameColor(intCandles.get(5), intCandles.get(5)));
        assertTrue(defaultAnalyzer.haveSameColor(intCandles.get(5), intCandles.get(4)));
        assertTrue(defaultAnalyzer.haveSameColor(intCandles.get(5), intCandles.get(0)));
        assertTrue(defaultAnalyzer.haveSameColor(intCandles.get(0), intCandles.get(5)));
    }

    @Test
    @DisplayName("[UNIT] Calculates the body overlap between two candles")
    void calculateBodyOverlapTest() {
        assertEquals(60, defaultAnalyzer.calculateBodyOverlap(intCandles.get(0), intCandles.get(1)));
        assertEquals(0, defaultAnalyzer.calculateBodyOverlap(intCandles.get(5), intCandles.get(0)));
        assertEquals(0, defaultAnalyzer.calculateBodyOverlap(intCandles.get(5), intCandles.get(5)));
        assertEquals(20, defaultAnalyzer.calculateBodyOverlap(intCandles.get(2), intCandles.get(1)));
        assertEquals(10, defaultAnalyzer.calculateBodyOverlap(intCandles.get(3), intCandles.get(3)));
    }

    @Test
    @DisplayName("[UNIT] Calculates the wick overlap between two candles")
    void calculateSameSideWickOverlapTest() {
        assertEquals(20, defaultAnalyzer.calculateSameSideWickOverlap(intCandles.get(0), intCandles.get(1)));
        assertEquals(0, defaultAnalyzer.calculateSameSideWickOverlap(intCandles.get(5), intCandles.get(0)));
        assertEquals(0, defaultAnalyzer.calculateSameSideWickOverlap(intCandles.get(5), intCandles.get(5)));
        assertEquals(10, defaultAnalyzer.calculateSameSideWickOverlap(intCandles.get(2), intCandles.get(1)));
        assertEquals(30, defaultAnalyzer.calculateSameSideWickOverlap(intCandles.get(3), intCandles.get(3)));
        assertEquals(20, defaultAnalyzer.calculateSameSideWickOverlap(intCandles.get(3), intCandles.get(6)));
        assertEquals(0, defaultAnalyzer.calculateSameSideWickOverlap(intCandles.get(4), intCandles.get(0)));
        assertEquals(15, defaultAnalyzer.calculateSameSideWickOverlap(intCandles.get(2), intCandles.get(6)));
        assertEquals(0, defaultAnalyzer.calculateSameSideWickOverlap(intCandles.get(4), intCandles.get(6)));
        assertEquals(0, defaultAnalyzer.calculateSameSideWickOverlap(intCandles.get(2), intCandles.get(4)));
        assertEquals(0, defaultAnalyzer.calculateSameSideWickOverlap(intCandles.get(1), intCandles.get(4)));
        assertEquals(15, defaultAnalyzer.calculateSameSideWickOverlap(intCandles.get(6), intCandles.get(7)));
    }

    @Test
    @DisplayName("[UNIT] Calculates overlap amount between integer intervals")
    void overlapAmountTest() {
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
    @DisplayName("[UNIT] Calculates span between 2 single candles")
    void calculateMonoCandleSpanTest() {

        DefaultAnalyzer localAnalyzer =
                new DefaultAnalyzer(Smoothing.NONE, 0, 0, false, false);

        List<IntCandle> intCandles1 = new ArrayList<>(List.of(intCandles.get(8)));
        List<IntCandle> intCandles2 = new ArrayList<>(List.of(intCandles.get(9)));

        assertEquals(100, localAnalyzer.calculateSurfaceSpan(intCandles1, intCandles2));
    }

    @Test
    @DisplayName("[UNIT] Calculates span between 2 list of multiple candles")
    void calculateMultiCandleSpanTest() {

        DefaultAnalyzer localAnalyzer =
                new DefaultAnalyzer(Smoothing.NONE, 0, 0, false, false);

        List<IntCandle> intCandles1 = new ArrayList<>(List.of(intCandles.get(1), intCandles.get(2), intCandles.get(3)));
        List<IntCandle> intCandles2 = new ArrayList<>(List.of(intCandles.get(2), intCandles.get(3), intCandles.get(4)));

        assertEquals(230, localAnalyzer.calculateSurfaceSpan(intCandles1, intCandles2));
    }

    @Test
    @DisplayName("[UNIT] Calculates surface match between 2 lists of candles")
    void calculateSurfaceMatch() {

        DefaultAnalyzer localAnalyzer =
                new DefaultAnalyzer(Smoothing.NONE, 0, 0, false, false);

        List<IntCandle> intCandlesToMatch1 = new ArrayList<>(List.of(intCandles.get(1), intCandles.get(2), intCandles.get(3)));
        List<IntCandle> intCandlesToMatch2 = new ArrayList<>(List.of(intCandles.get(2), intCandles.get(3), intCandles.get(4)));

        assertEquals(30, localAnalyzer.calculateSurfaceMatch(intCandlesToMatch1, intCandlesToMatch2));
        assertEquals(30, localAnalyzer.calculateSurfaceMatch(intCandlesToMatch2, intCandlesToMatch1));
    }

    @Test
    @DisplayName("[UNIT] Calculates match score between 2 single candles")
    void calculateMonoCandleMatchScoreTest() {

        DefaultAnalyzer localAnalyzer =
                new DefaultAnalyzer(Smoothing.NONE, 0, 0, false, false);

        List<IntCandle> intCandlesToMatch1 = new ArrayList<>(List.of(intCandles.get(1)));
        List<IntCandle> intCandlesToMatch2 = new ArrayList<>(List.of(intCandles.get(2)));

        assertEquals(33, localAnalyzer.calculateMatchScore(intCandlesToMatch1, intCandlesToMatch2));
        assertEquals(33, localAnalyzer.calculateMatchScore(intCandlesToMatch2, intCandlesToMatch1));
    }

    @Test
    @DisplayName("[UNIT] Calculates match score between 2 lists of multiple candles")
    void calculateMultiCandleMatchScoreTest() {

        DefaultAnalyzer localAnalyzer =
                new DefaultAnalyzer(Smoothing.NONE, 0, 0, false, false);

        List<IntCandle> intCandlesToMatch1 = new ArrayList<>(List.of(intCandles.get(1), intCandles.get(2), intCandles.get(3)));
        List<IntCandle> intCandlesToMatch2 = new ArrayList<>(List.of(intCandles.get(2), intCandles.get(3), intCandles.get(4)));

        assertEquals(13, localAnalyzer.calculateMatchScore(intCandlesToMatch1, intCandlesToMatch2));
        assertEquals(13, localAnalyzer.calculateMatchScore(intCandlesToMatch2, intCandlesToMatch1));
    }

    @Test
    @DisplayName("[UNIT] Calculates extrapolated match score")
    void calculateExtrapolatedMatchScoreTest() {

        List<IntCandle> intCandlesToMatch1 = new ArrayList<>(List.of(intCandles.get(1)));
        List<IntCandle> intCandlesToMatch2 = new ArrayList<>(List.of(intCandles.get(2)));

        DefaultAnalyzer localAnalyzer =
                new DefaultAnalyzer(Smoothing.NONE, 0, 0, false, true);

        assertEquals(43, localAnalyzer.calculateMatchScore(intCandlesToMatch1, intCandlesToMatch2));
    }

    @Test
    @DisplayName("[UNIT] Calculates match score with broken values")
    void calculateMatchScoreWithBrokenListsTest() {

        DefaultAnalyzer localAnalyzer =
                new DefaultAnalyzer(Smoothing.NONE, 0, 0, false, false);

        List<IntCandle> intCandlesToMatch1 = new ArrayList<>(Collections.emptyList());
        List<IntCandle> intCandlesToMatch2 = new ArrayList<>(List.of(intCandles.get(2)));

        assertEquals(0, localAnalyzer.calculateMatchScore(intCandlesToMatch1, intCandlesToMatch2));

        intCandlesToMatch1 = new ArrayList<>(List.of(intCandles.get(2)));
        intCandlesToMatch2 = new ArrayList<>(Collections.emptyList());

        assertEquals(0, localAnalyzer.calculateMatchScore(intCandlesToMatch1, intCandlesToMatch2));

        intCandlesToMatch1 = intCandlesToMatch2;

        assertEquals(0, localAnalyzer.calculateMatchScore(intCandlesToMatch1, intCandlesToMatch2));
    }

    @Test
    @DisplayName("[UNIT] Filters price variation")
    void filterPriceVariationTest() {

        DefaultAnalyzer localAnalyzer = new DefaultAnalyzer(Smoothing.NONE, 0, 5, false, false);
        assertEquals(0, localAnalyzer.filterPriceVariation(4));

        localAnalyzer = new DefaultAnalyzer(Smoothing.NONE, 0, 5, true, false);
        assertEquals(-5.25f, localAnalyzer.filterPriceVariation(-5));
    }


    @Test
    @DisplayName("[UNIT] Calculates basic match score with different smoothing values")
    void calculateMatchScoreBasicWithSmoothingVariationsTest() {

        List<IntCandle> intCandlesToMatch1 = new ArrayList<>(List.of(intCandles.get(0), intCandles.get(1)));
        List<IntCandle> intCandlesToMatch2 = new ArrayList<>(List.of(intCandles.get(2), intCandles.get(3)));

        DefaultAnalyzer localAnalyzer =
                new DefaultAnalyzer(Smoothing.NONE, 0, 0, false, false);
        int noSmoothingMatchScore = localAnalyzer.calculateMatchScore(intCandlesToMatch1, intCandlesToMatch2);

        localAnalyzer =
                new DefaultAnalyzer(Smoothing.LOGARITHMIC, 0, 0, false, false);
        int logarithmicMatchScore = localAnalyzer.calculateMatchScore(intCandlesToMatch1, intCandlesToMatch2);

        localAnalyzer =
                new DefaultAnalyzer(Smoothing.LINEAR, 0, 0, false, false);
        int linearSmoothingMatchScore = localAnalyzer.calculateMatchScore(intCandlesToMatch1, intCandlesToMatch2);

        localAnalyzer =
                new DefaultAnalyzer(Smoothing.EXPONENTIAL, 0, 0, false, false);
        int exponentialSmoothingMatchScore = localAnalyzer.calculateMatchScore(intCandlesToMatch1, intCandlesToMatch2);

        assertEquals(22, noSmoothingMatchScore);
        assertEquals(18, logarithmicMatchScore);
        assertEquals(15, linearSmoothingMatchScore);
        assertEquals(13, exponentialSmoothingMatchScore);
    }

}
