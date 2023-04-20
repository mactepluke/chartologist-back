package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.DataConfigTest;
import com.syngleton.chartomancy.analytics.ComputationSettings;
import com.syngleton.chartomancy.analytics.ComputationType;
import com.syngleton.chartomancy.factory.PatternSettings;
import com.syngleton.chartomancy.model.*;
import com.syngleton.chartomancy.util.Format;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
class PatternServicesTests {

    private final static int TEST_GRAPH_LENGTH = 3000;
    private final static int TEST_GRAPH_STARTING_DATETIME = 1666051200;
    private final static Timeframe TEST_TIMEFRAME = Timeframe.DAY;
    private final static Symbol TEST_SYMBOL = Symbol.UNDEFINED;
    private final static int TEST_STARTING_OPEN = 2000;
    private final static int MINIMUM_VALUE = 400;
    private final static int MAXIMUM_VALUE = 5000;
    private final static float VARIABILITY_COEF = 300;

    private Graph mockGraph;
    private List<Pattern> patterns;
    private List<Pattern> patternsToPrint;
    private PatternSettings.Builder testPatternSettingsBuilder;
    private ComputationSettings.Builder testComputationSettingsBuilder;

    @Autowired
    PatternService patternService;
    @MockBean
    DataService dataService;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING PATTERN SERVICE TESTS ***");
        List<Candle> mockCandles = new ArrayList<>();

        for (int i = 0; i < TEST_GRAPH_LENGTH; i++) {
            Random rd = new Random();
            float span = (float) Math.random() * VARIABILITY_COEF;
            boolean direction = rd.nextBoolean();
            float open = (i == 0) ? TEST_STARTING_OPEN : mockCandles.get(i - 1).close();
            float close = (direction) ? open + span : open - span;
            float high = (float) ((direction) ? close + Math.random() * VARIABILITY_COEF / 2 : open + Math.random() * VARIABILITY_COEF / 2);
            float low = (float) ((direction) ? open - Math.random() * VARIABILITY_COEF / 2 : close - Math.random() * VARIABILITY_COEF / 2);
            float volume = (float) Math.random() * VARIABILITY_COEF * TEST_STARTING_OPEN;

            Candle candle = new Candle(
                    LocalDateTime.ofEpochSecond(
                            TEST_GRAPH_STARTING_DATETIME + TEST_TIMEFRAME.durationInSeconds * i,
                            0,
                            ZoneOffset.UTC),
                    Format.streamlineFloat(open, MINIMUM_VALUE, MAXIMUM_VALUE),
                    Format.streamlineFloat(high, MINIMUM_VALUE, MAXIMUM_VALUE),
                    Format.streamlineFloat(low, MINIMUM_VALUE, MAXIMUM_VALUE),
                    Format.streamlineFloat(close, MINIMUM_VALUE, MAXIMUM_VALUE),
                    volume
            );
            mockCandles.add(candle);
        }

        mockGraph = new Graph("Mock graph", TEST_SYMBOL, TEST_TIMEFRAME, mockCandles);

        testPatternSettingsBuilder = new PatternSettings.Builder()
                .autoconfig(PatternSettings.Autoconfig.TEST)
                .graph(mockGraph);
        testComputationSettingsBuilder = new ComputationSettings.Builder()
                .autoconfig(ComputationSettings.Autoconfig.TEST)
                .graph(mockGraph);
    }

    @AfterAll
    void tearDown() {
        log.trace(patternService.generatePatternsToPrint(patternsToPrint));
        log.info("*** ENDING PATTERN SERVICE TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Create basic patterns from mock graph")
    void createBasicPatternsTest() {

        patterns = patternService.create(testPatternSettingsBuilder.patternType(PatternType.BASIC));

        assertFalse(patterns.isEmpty());
        assertEquals(TEST_GRAPH_LENGTH / testPatternSettingsBuilder.build().getLength(), patterns.size());
    }

    @Test
    @DisplayName("[UNIT] Create predictive patterns from mock graph")
    void createPredictivePatternsTest() {

        patterns = patternService.create(testPatternSettingsBuilder.patternType(PatternType.PREDICTIVE));

        assertFalse(patterns.isEmpty());
        assertEquals(TEST_GRAPH_LENGTH / testPatternSettingsBuilder.build().getLength(), patterns.size());
    }

    @Test
    @DisplayName("[IT] Compute patterns from created predictive patterns")
    void computeBasicIterationTest() {

        patterns = patternService.create(testPatternSettingsBuilder.patternType(PatternType.PREDICTIVE));
        patterns = patternService.compute(testComputationSettingsBuilder
                .patterns(patterns)
                .computationType(ComputationType.BASIC_ITERATION)
        );

        assertFalse(patterns.isEmpty());

        for (Pattern pattern : patterns) {
            assertEquals(1, ((PredictivePattern) pattern).getComputationsHistory().size());
            assertEquals(mockGraph.getCandles().size() - pattern.getLength() - ((PredictivePattern) pattern).getScope(),
                    ((PredictivePattern) pattern).getComputationsHistory().get(0).computations());
        }
        patternsToPrint = patterns;
    }
}
