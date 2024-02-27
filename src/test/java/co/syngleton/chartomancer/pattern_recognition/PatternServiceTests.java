package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.charting.CandleRescaler;
import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.configuration.MockConfig;
import co.syngleton.chartomancer.configuration.MockData;
import co.syngleton.chartomancer.configuration.MockDataConfig;
import co.syngleton.chartomancer.core_entities.*;
import lombok.extern.log4j.Log4j2;
import me.tongfei.progressbar.ProgressBar;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {MockConfig.class, MockDataConfig.class})
@ActiveProfiles("test")
class PatternServiceTests {

    @Autowired
    PatternService patternService;
    @Autowired
    MockData mockData;
    @MockBean(name = "analyzer")
    PatternRecognitionAnalyzer analyzer;
    @MockBean(name = "candleRescaler")
    CandleRescaler candleRescaler;

    CoreData coreData;

    private List<Pattern> patterns;
    private PatternSettings.Builder testPatternSettingsBuilder;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING PATTERN SERVICE TESTS ***");

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
    @DisplayName("[UNIT] Create predictive patterns from mock graph")
    void createPredictivePatternsTest() {

        patterns = patternService.createPatterns(testPatternSettingsBuilder.patternType(PatternSettings.PatternType.PREDICTIVE));

        assertFalse(patterns.isEmpty());
        assertEquals(mockData.getTestGraphLength() / testPatternSettingsBuilder.build().getLength(), patterns.size());
    }

    @Test
    @DisplayName("[UNIT] Create predictive patterns from mock graph")
    void createMultiPredictivePatternsTest() {

        patterns = patternService.createPatterns(testPatternSettingsBuilder.patternType(PatternSettings.PatternType.MULTI_PREDICTIVE));

        assertFalse(patterns.isEmpty());
        assertEquals(mockData.getTestGraphLength() / testPatternSettingsBuilder.build().getLength(), patterns.size());
    }

    @Test
    @DisplayName("[UNIT] Computes basic iteration predictive patterns")
    void computesBasicIterationPredictivePatternsTest() {

        int scope = 10;

        ComputationSettings computationSettings = new ComputationSettings.Builder()
                .graph(mockData.getMockGraphDay1())
                .autoconfig(ComputationSettings.Autoconfig.TEST)
                .build();


        ComputablePattern testPattern = new ComputablePattern(new BasicPattern(
                mockData.getIntCandles(),
                100,
                Symbol.UNDEFINED,
                Timeframe.UNKNOWN),
                scope);


        when(analyzer.calculatePriceVariation(any(), anyInt())).thenReturn(8f);
        when(analyzer.filterPriceVariation(anyFloat())).thenAnswer(invocation -> invocation.getArgument(0));
        when(analyzer.calculateMatchScore(any(), any())).thenReturn(50);
        when(candleRescaler.rescale(any(), anyInt())).thenReturn(Collections.emptyList());

        Pattern pattern = patternService.computeBasicIterationPattern(
                testPattern,
                computationSettings,
                new ProgressBar("Test", 100)
        );

        assertEquals(8, Math.round(((ComputablePattern) pattern).getPriceVariationPrediction()));
    }

    @Test
    @DisplayName("[UNIT] Computes basic iteration multi-predictive patterns")
    void computesBasicIterationMultiPredictivePatternsTest() {

        int scope = 10;

        ComputationSettings computationSettings = new ComputationSettings.Builder()
                .graph(mockData.getMockGraphDay1())
                .autoconfig(ComputationSettings.Autoconfig.TEST)
                .build();


        MultiComputablePattern testPattern = new MultiComputablePattern(new BasicPattern(
                mockData.getIntCandles(),
                100,
                Symbol.UNDEFINED,
                Timeframe.UNKNOWN),
                scope);

        when(analyzer.calculatePriceVariation(any(), anyInt())).thenReturn(8f);
        when(analyzer.filterPriceVariation(anyFloat())).thenAnswer(invocation -> invocation.getArgument(0));
        when(analyzer.calculateMatchScore(any(), any())).thenReturn(50);
        when(candleRescaler.rescale(any(), anyInt())).thenReturn(Collections.emptyList());

        log.debug("Test Pattern: {}", testPattern);

        Pattern pattern = patternService.computeBasicIterationPattern(
                testPattern,
                computationSettings,
                new ProgressBar("Test", 100)
        );


        log.debug("Pattern: {}", pattern);

        assertEquals(8, Math.round(((MultiComputablePattern) pattern).getPriceVariationPrediction()));
        for (int i = 1; i <= scope; i++) {
            assertEquals(8, Math.round(((MultiComputablePattern) pattern).getPriceVariationPrediction(i)));
        }
    }
}
