package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.configuration.MockConfig;
import co.syngleton.chartomancer.core_entities.FloatCandle;
import co.syngleton.chartomancer.core_entities.Graph;
import co.syngleton.chartomancer.core_entities.IntCandle;
import co.syngleton.chartomancer.exception.InvalidParametersException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = MockConfig.class)
@ActiveProfiles("test")
class ChartingServiceTests {

    @Autowired
    ChartingService chartingService;
    private List<FloatCandle> floatCandles;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING CHARTING SERVICE TESTS ***");

        LocalDateTime candleDate = LocalDateTime.now();

        FloatCandle floatCandle0 = new FloatCandle(candleDate, 20, 100, 0, 80, 20);
        FloatCandle floatCandle1 = new FloatCandle(candleDate, 20, 90, 10, 80, 20);
        FloatCandle floatCandle2 = new FloatCandle(candleDate, 40, 100, 40, 60, 2000);
        FloatCandle floatCandle3 = new FloatCandle(candleDate, 50, 60, 20, 40, 30);
        FloatCandle floatCandle4 = new FloatCandle(candleDate, 80, 80, 50, 70, 15);
        FloatCandle floatCandle5 = new FloatCandle(candleDate, 0, 0, 0, 0, 0);
        FloatCandle floatCandle6 = new FloatCandle(candleDate, 45, 75, 30, 48, 25);
        FloatCandle floatCandle7 = new FloatCandle(candleDate, 50, 65, 35, 60, 20);
        FloatCandle floatCandle8 = new FloatCandle(candleDate, 80, 100, 60, 70, 10);
        FloatCandle floatCandle9 = new FloatCandle(candleDate, 20, 80, 0, 40, 10);

        floatCandles = new ArrayList<>(
                List.of(floatCandle0,
                        floatCandle1,
                        floatCandle2,
                        floatCandle3,
                        floatCandle4,
                        floatCandle5,
                        floatCandle6,
                        floatCandle7,
                        floatCandle8,
                        floatCandle9)
        );
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING CHARTING SERVICE TESTS ***");

    }


    @Test
    @DisplayName("[UNIT] Rescale candles first test")
    void rescaleToIntCandleFirstTest() {

        IntCandle intCandle = chartingService.rescaleToIntCandle(floatCandles.get(0), 50, 0, 100, 0, 100);

        assertEquals(10, intCandle.open());
        assertEquals(50, intCandle.high());
        assertEquals(0, intCandle.low());
        assertEquals(40, intCandle.close());
        assertEquals(10, intCandle.volume());
    }

    @Test
    @DisplayName("[UNIT] Rescale candles second test")
    void rescaleToIntCandleSecondTest() {

        IntCandle intCandle = chartingService.rescaleToIntCandle(floatCandles.get(1), 100, 0, 100, 0, 100);

        assertEquals(20, intCandle.open());
        assertEquals(90, intCandle.high());
        assertEquals(10, intCandle.low());
        assertEquals(80, intCandle.close());
        assertEquals(20, intCandle.volume());
    }

    @Test
    @DisplayName("[UNIT] Rescale candles third test")
    void rescaleToIntCandleThirdTest() {

        IntCandle intCandle = chartingService.rescaleToIntCandle(floatCandles.get(2), 100, 0, 200, 0, 100);

        assertEquals(20, intCandle.open());
        assertEquals(50, intCandle.high());
        assertEquals(20, intCandle.low());
        assertEquals(30, intCandle.close());
        assertEquals(100, intCandle.volume());
    }

    @Test
    @DisplayName("[UNIT] Rescale candles fourth test")
    void rescaleToIntCandleFourthTest() {

        IntCandle intCandle = chartingService.rescaleToIntCandle(floatCandles.get(3), 100, 5, 10, 0, 50);

        assertEquals(100, intCandle.open());
        assertEquals(100, intCandle.high());
        assertEquals(100, intCandle.low());
        assertEquals(100, intCandle.close());
        assertEquals(60, intCandle.volume());
    }

    @Test
    @DisplayName("[UNIT] Rescale candles fifth test")
    void rescaleToIntCandleFifthTest() {

        IntCandle intCandle = chartingService.rescaleToIntCandle(floatCandles.get(4), 100, 50, 80, 0, 100);

        assertEquals(100, intCandle.open());
        assertEquals(100, intCandle.high());
        assertEquals(0, intCandle.low());
        assertEquals(67, intCandle.close());
        assertEquals(15, intCandle.volume());
    }

    @Test
    @DisplayName("[UNIT] Rescale candles sixth test")
    void rescaleToIntCandleSixthTest() {

        IntCandle intCandle = chartingService.rescaleToIntCandle(floatCandles.get(5), 50, 22, 134, 0, 100);

        assertEquals(0, intCandle.open());
        assertEquals(0, intCandle.high());
        assertEquals(0, intCandle.low());
        assertEquals(0, intCandle.close());
        assertEquals(0, intCandle.volume());
    }

    @Test
    @DisplayName("[UNIT] Streamlines float to int candles")
    void streamlineToIntCandlesTest() {

        List<FloatCandle> floatCandles = new ArrayList<>(List.of(this.floatCandles.get(0)));

        List<IntCandle> intCandles = chartingService.rescale(floatCandles, 100);

        log.debug(chartingService.rescaleToIntCandle(this.floatCandles.get(0), 100, 0, 100, 0, 100));

        assertEquals(1, intCandles.size());
        assertEquals(20, intCandles.get(0).open());
        assertEquals(100, intCandles.get(0).high());
        assertEquals(0, intCandles.get(0).low());
        assertEquals(80, intCandles.get(0).close());
        assertEquals(0, intCandles.get(0).volume());
    }

    @Test
    @DisplayName("[UNIT] Get extreme values of a list of candles, first test")
    void getLowestAndHighestCandleValuesAndVolumesFirstTest() {

        List<Float> extremes = chartingService.getLowestAndHighestCandleValuesAndVolumes(floatCandles);

        assertEquals(0, extremes.get(0));
        assertEquals(100, extremes.get(1));
        assertEquals(0, extremes.get(2));
        assertEquals(2000, extremes.get(3));
    }

    @Test
    @DisplayName("[UNIT] Get extreme values of a list of candles, second test")
    void getLowestAndHighestCandleValuesAndVolumesSecondTest() {

        List<Float> extremes = chartingService.getLowestAndHighestCandleValuesAndVolumes(
                List.of(floatCandles.get(3), floatCandles.get(4), floatCandles.get(6)));

        assertEquals(20, extremes.get(0));
        assertEquals(80, extremes.get(1));
        assertEquals(15, extremes.get(2));
        assertEquals(30, extremes.get(3));
    }

    @Test
    @DisplayName("[UNIT] Creates an upscale candle from a graph, first test")
    void createUpscaleCandleFirstTest() {

        Graph graph = new Graph(
                "testGraph",
                Symbol.UNDEFINED,
                Timeframe.UNKNOWN,
                List.of(floatCandles.get(2), floatCandles.get(3), floatCandles.get(4)));

        FloatCandle floatCandle = chartingService.createUpscaleCandle(graph, 3, 0);

        assertEquals(40, floatCandle.open());
        assertEquals(100, floatCandle.high());
        assertEquals(20, floatCandle.low());
        assertEquals(70, floatCandle.close());
        assertEquals(2045, floatCandle.volume());
    }

    @Test
    @DisplayName("[UNIT] Creates an upscale candle from a graph, second test")
    void createUpscaleCandleSecondTest() {

        Graph graph = new Graph(
                "testGraph",
                Symbol.UNDEFINED,
                Timeframe.UNKNOWN,
                List.of(floatCandles.get(2), floatCandles.get(3), floatCandles.get(4)));

        FloatCandle floatCandle = chartingService.createUpscaleCandle(graph, 4, 0);

        assertNull(floatCandle);
    }

    @Test
    @DisplayName("[UNIT] Upscales a graph to a superior timeframe, first test")
    void upscaleToTimeFrameFirstTest() {

        Graph graph = new Graph(
                "testGraph",
                Symbol.UNDEFINED,
                Timeframe.HOUR,
                floatCandles);

        Graph upscaleGraph = chartingService.upscaleToTimeFrame(graph, Timeframe.FOUR_HOUR);

        assertEquals(Timeframe.FOUR_HOUR, upscaleGraph.getTimeframe());
        assertEquals(2, upscaleGraph.getFloatCandles().size());
    }

    @Test
    @DisplayName("[UNIT] Upscales a graph to a superior timeframe, second test")
    void upscaleToTimeFrameSecondTest() {

        Graph graph = new Graph(
                "testGraph",
                Symbol.UNDEFINED,
                Timeframe.DAY,
                floatCandles);

        assertThrows(InvalidParametersException.class, () -> chartingService.upscaleToTimeFrame(graph, Timeframe.FOUR_HOUR));
    }

    @Test
    @DisplayName("[UNIT] Upscales a graph to a superior timeframe, third test")
    void upscaleToTimeFrameThirdTest() {

        Graph graph = new Graph(
                "testGraph",
                Symbol.UNDEFINED,
                Timeframe.HALF_HOUR,
                floatCandles);

        Graph upscaleGraph = chartingService.upscaleToTimeFrame(graph, Timeframe.FOUR_HOUR);

        assertEquals(Timeframe.FOUR_HOUR, upscaleGraph.getTimeframe());
        assertEquals(1, upscaleGraph.getFloatCandles().size());
    }

    @Test
    @DisplayName("[UNIT] Repairs a graph's missing candles")
    void repairMissingCandles() {

        LocalDateTime candleDate0 = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        LocalDateTime candleDate1 = candleDate0.plusHours(1);
        LocalDateTime candleDate2 = candleDate0.plusHours(2);
        LocalDateTime candleDate3 = candleDate0.plusHours(3);

        FloatCandle floatCandle0 = new FloatCandle(candleDate0, 250, 430, 250, 400, 50);
        FloatCandle floatCandle1 = new FloatCandle(candleDate1, 400, 410, 200, 290, 80);
        FloatCandle floatCandle2 = new FloatCandle(candleDate2, 290, 390, 40, 330, 30);
        FloatCandle floatCandle3 = new FloatCandle(candleDate3, 330, 415, 322, 415, 10);

        List<FloatCandle> discontinuousCandles = List.of(floatCandle0, floatCandle1, floatCandle3);

        Graph graph = new Graph(
                "testGraph",
                Symbol.UNDEFINED,
                Timeframe.HOUR,
                discontinuousCandles);

        Graph repairedGraph = chartingService.repairMissingCandles(graph);

        assertNotNull(repairedGraph);
        assertEquals(4, repairedGraph.getFloatCandles().size());
        assertEquals(floatCandle2.dateTime(), repairedGraph.getFloatCandles().get(2).dateTime());
        assertEquals(floatCandle2.open(), repairedGraph.getFloatCandles().get(2).open());
        assertEquals(floatCandle2.close(), repairedGraph.getFloatCandles().get(2).close());
        assertEquals(floatCandle2.open(), repairedGraph.getFloatCandles().get(2).low());
        assertEquals(floatCandle2.close(), repairedGraph.getFloatCandles().get(2).high());
        assertEquals(45, repairedGraph.getFloatCandles().get(2).volume());
    }

}
