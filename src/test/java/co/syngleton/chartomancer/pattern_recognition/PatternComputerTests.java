package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.analytics.AnalyzerConfigTest;
import co.syngleton.chartomancer.charting.CandleRescaler;
import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.data.DataConfigTest;
import co.syngleton.chartomancer.shared_domain.*;
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

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {DataConfigTest.class, AnalyzerConfigTest.class})
@ActiveProfiles("test")
class PatternComputerTests {

    private static final int GRANULARITY = 100;
    @Autowired
    CandleRescaler candleRescaler;
    @Autowired
    PatternComputer patternComputer;
    private LocalDateTime candleDate;
    private List<FloatCandle> floatCandles;
    private List<IntCandle> intCandles;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING PATTERN COMPUTER TESTS ***");

        candleDate = LocalDateTime.now();
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING PATTERN COMPUTER TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Streamlines float to int candles")
    void streamlineToIntCandlesTest() {
        FloatCandle floatCandle = new FloatCandle(candleDate, 20, 100, 0, 80, 20);
        floatCandles = new ArrayList<>(List.of(floatCandle));

        intCandles = candleRescaler.rescale(floatCandles, GRANULARITY);

        assertEquals(1, intCandles.size());
        assertEquals(20, intCandles.get(0).open());
        assertEquals(100, intCandles.get(0).high());
        assertEquals(0, intCandles.get(0).low());
        assertEquals(80, intCandles.get(0).close());
        assertEquals(20, intCandles.get(0).volume());
    }

    @Test
    @DisplayName("[IT] Computes Light Predictive patterns")
    void computesLightPredictivePatternTest() {


        FloatCandle floatCandle1 = new FloatCandle(candleDate, 20, 90, 10, 80, 20);
        FloatCandle floatCandle2 = new FloatCandle(candleDate, 40, 100, 40, 60, 20);

        floatCandles = new ArrayList<>(List.of(floatCandle1, floatCandle2));

        Graph graph = new Graph("Computer test graph", Symbol.UNDEFINED, Timeframe.HOUR, floatCandles);


        intCandles = candleRescaler.rescale(new ArrayList<>(List.of(floatCandle1)), GRANULARITY);

        BasicPattern basicPattern = new BasicPattern(intCandles, GRANULARITY, 1, Symbol.UNDEFINED, Timeframe.HOUR, candleDate);
        PredictivePattern lightPredictivePattern = new PredictivePattern(basicPattern, 1);

        List<Pattern> patterns = new ArrayList<>(List.of(lightPredictivePattern));

        ComputationSettings.Builder computationBuilder = new ComputationSettings.Builder();

        patternComputer.computePatterns(computationBuilder
                .patterns(patterns)
                .graph(graph)
                .autoconfig(ComputationSettings.Autoconfig.TEST));

        PredictivePattern resultPattern = (PredictivePattern) patterns.get(0);

        //assertEquals(1, resultPattern.getComputationsHistory().get(0).computations());
        assertEquals(25, resultPattern.getPriceVariationPrediction());
    }

}
