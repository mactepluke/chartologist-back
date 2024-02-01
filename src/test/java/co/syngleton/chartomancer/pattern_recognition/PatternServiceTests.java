package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.charting.CandleRescaler;
import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.core_entities.*;
import co.syngleton.chartomancer.data.DataConfigTest;
import co.syngleton.chartomancer.data.DataProcessor;
import co.syngleton.chartomancer.data.MockData;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
@ActiveProfiles("test")
class PatternServiceTests {

    @Autowired
    PatternService patternService;
    @Autowired
    CandleRescaler candleRescaler;
    @Autowired
    MockData mockData;
    CoreData coreData;
    @Autowired
    DataProcessor dataProcessor;
    private List<Pattern> patterns;
    private PatternSettings.Builder testPatternSettingsBuilder;
    private LocalDateTime candleDate;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING PATTERN SERVICE TESTS ***");

        candleDate = LocalDateTime.now();
        coreData = DefaultCoreData.newInstance();
        coreData.purgeUselessData(PurgeOption.GRAPHS_AND_PATTERNS);
        mockData.getTestGraphs().forEach(graph -> coreData.addGraph(graph));

        testPatternSettingsBuilder = new PatternSettings.Builder()
                .autoconfig(PatternSettings.Autoconfig.TEST)
                .graph(mockData.getMockGraphDay1());
    }

    @AfterAll
    void tearDown() {
        coreData = null;
        mockData.resetGraphs();
        log.info("*** ENDING PATTERN SERVICE TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Create light basic patterns from mock graph")
    void createBasicPatternsTest() {

        patterns = patternService.createPatterns(testPatternSettingsBuilder.patternType(PatternSettings.PatternType.BASIC));

        assertFalse(patterns.isEmpty());
        assertEquals(mockData.getTestGraphLength() / testPatternSettingsBuilder.build().getLength(), patterns.size());
    }

    @Test
    @DisplayName("[UNIT] Create light predictive patterns from mock graph")
    void createPredictivePatternsTest() {

        patterns = patternService.createPatterns(testPatternSettingsBuilder.patternType(PatternSettings.PatternType.PREDICTIVE));

        assertFalse(patterns.isEmpty());
        assertEquals(mockData.getTestGraphLength() / testPatternSettingsBuilder.build().getLength(), patterns.size());
    }

    @Test
    @DisplayName("[IT] Creates and computes pattern boxes from mock graphs")
    void createAndComputePatternBoxes() {

        log.debug(coreData);
        assertTrue(dataProcessor.createPatternBoxes(coreData, new PatternSettings.Builder()
                .patternType(PatternSettings.PatternType.PREDICTIVE)
                .autoconfig(PatternSettings.Autoconfig.TEST)));
        assertEquals(mockData.getNumberOfDifferentMockTimeframes(), coreData.getNumberOfPatternSets());
        assertTrue(patternService.computeCoreData(coreData, new ComputationSettings.Builder().autoconfig(ComputationSettings.Autoconfig.TEST)));
        assertEquals(mockData.getNumberOfDifferentMockTimeframes(), coreData.getNumberOfPatternSets());
    }

    @Test
    @DisplayName("[IT] Computes Light Predictive patterns")
    void computesLightPredictivePatternTest() {

        List<FloatCandle> floatCandles;
        List<IntCandle> intCandles;

        FloatCandle floatCandle1 = new FloatCandle(candleDate, 20, 90, 10, 80, 20);
        FloatCandle floatCandle2 = new FloatCandle(candleDate, 40, 100, 40, 60, 20);

        floatCandles = new ArrayList<>(List.of(floatCandle1, floatCandle2));

        Graph graph = new Graph("Computer test graph", Symbol.UNDEFINED, Timeframe.HOUR, floatCandles);


        intCandles = candleRescaler.rescale(new ArrayList<>(List.of(floatCandle1)), 100);

        BasicPattern basicPattern = new BasicPattern(intCandles, 100, 1, Symbol.UNDEFINED, Timeframe.HOUR, candleDate);
        PredictivePattern lightPredictivePattern = new PredictivePattern(basicPattern, 1);

        List<Pattern> patterns = new ArrayList<>(List.of(lightPredictivePattern));

        ComputationSettings.Builder computationBuilder = new ComputationSettings.Builder();

        patternService.computePatterns(computationBuilder
                .patterns(patterns)
                .graph(graph)
                .autoconfig(ComputationSettings.Autoconfig.TEST));

        PredictivePattern resultPattern = (PredictivePattern) patterns.get(0);

        assertEquals(25, resultPattern.getPriceVariationPrediction());
    }
}
