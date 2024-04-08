package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.charting.CandleRescaler;
import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.configuration.MockData;
import co.syngleton.chartomancer.configuration.MockDataConfig;
import co.syngleton.chartomancer.core_entities.*;
import co.syngleton.chartomancer.data.DataProcessor;
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
@ContextConfiguration(classes = MockDataConfig.class)
@ActiveProfiles("test")
class PatternRecognitionITests {

    @Autowired
    DefaultPatternService patternService;
    @Autowired
    MockData mockData;
    @Autowired
    DataProcessor dataProcessor;
    @Autowired
    CandleRescaler candleRescaler;

    CoreData coreData;

    private LocalDateTime candleDate;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING PATTERN SERVICE INTEGRATION TESTS ***");

        candleDate = LocalDateTime.now();
        coreData = DefaultCoreData.newInstance();
        coreData.purgeUselessData(PurgeOption.GRAPHS_AND_PATTERNS);
        mockData.getTestGraphs().forEach(graph -> coreData.addGraph(graph));
    }

    @AfterAll
    void tearDown() {
        coreData = null;
        mockData.resetGraphs();
        log.info("*** ENDING PATTERN SERVICE INTEGRATION TESTS ***");
    }

    @Test
    @DisplayName("[IT] Creates and computes pattern boxes from mock graphs")
    void createAndComputePatternsTest() {

        log.debug(coreData);
        assertTrue(dataProcessor.createPatternsForCoreData(coreData, new PatternSettings.Builder()
                .patternType(PatternSettings.PatternType.PREDICTIVE)
                .autoconfig(PatternSettings.Autoconfig.TEST)));
        assertEquals(mockData.getNumberOfDifferentMockTimeframes(), coreData.getNumberOfPatternSets());
        assertTrue(patternService.computeCoreData(
                coreData, new ComputationSettings.Builder().autoconfig(ComputationSettings.Autoconfig.TEST)));
        assertEquals(mockData.getNumberOfDifferentMockTimeframes(), coreData.getNumberOfPatternSets());
    }

    @Test
    @DisplayName("[IT] Computes patterns")
    void computePatternTest() {

        List<FloatCandle> floatCandles;
        List<IntCandle> intCandles;

        FloatCandle floatCandle1 = new FloatCandle(candleDate, 20, 90, 10, 80, 20);
        FloatCandle floatCandle2 = new FloatCandle(candleDate, 40, 100, 40, 60, 20);

        floatCandles = new ArrayList<>(List.of(floatCandle1, floatCandle2));

        Graph graph = new Graph("Computer test graph", Symbol.UNDEFINED, Timeframe.HOUR, floatCandles);


        intCandles = candleRescaler.rescale(new ArrayList<>(List.of(floatCandle1)), 100);

        BasicPattern basicPattern = new BasicPattern(intCandles, 100, Symbol.UNDEFINED, Timeframe.HOUR);
        ComputablePattern predictivePattern = new ComputablePattern(basicPattern, 1);

        List<Pattern> patterns = new ArrayList<>(List.of(predictivePattern));

        ComputationSettings.Builder computationBuilder = new ComputationSettings.Builder();

        patternService.computePatterns(computationBuilder
                .patterns(patterns)
                .graph(graph)
                .autoconfig(ComputationSettings.Autoconfig.TEST));

        ComputablePattern resultPattern = (ComputablePattern) patterns.get(0);

        assertEquals(50, resultPattern.getPriceVariationPrediction());
    }
}
