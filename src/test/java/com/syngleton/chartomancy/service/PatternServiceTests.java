package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.configuration.DataConfigTest;
import com.syngleton.chartomancy.configuration.MockData;
import com.syngleton.chartomancy.analytics.ComputationSettings;
import com.syngleton.chartomancy.analytics.ComputationType;
import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.factory.PatternSettings;
import com.syngleton.chartomancy.model.charting.patterns.ComputablePattern;
import com.syngleton.chartomancy.model.charting.patterns.Pattern;
import com.syngleton.chartomancy.model.charting.patterns.PatternType;
import com.syngleton.chartomancy.model.charting.patterns.PredictivePattern;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
class PatternServiceTests {

    private List<Pattern> patterns;
    private PatternSettings.Builder testPatternSettingsBuilder;
    private ComputationSettings.Builder testComputationSettingsBuilder;

    @Autowired
    PatternService patternService;
    @Autowired
    MockData mockData;
    @Autowired
    CoreData coreData;
    @Autowired
    DataService dataService;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING PATTERN SERVICE TESTS ***");

        coreData.setGraphs(mockData.getTestGraphs());

        testPatternSettingsBuilder = new PatternSettings.Builder()
                .autoconfig(PatternSettings.Autoconfig.TEST)
                .graph(mockData.getMockGraphDay1());
        testComputationSettingsBuilder = new ComputationSettings.Builder()
                .autoconfig(ComputationSettings.Autoconfig.TEST)
                .graph(mockData.getMockGraphDay1());
    }

    @AfterAll
    void tearDown() {
        coreData = null;
        mockData.resetGraphs();
        log.info("*** ENDING PATTERN SERVICE TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Create basic patterns from mock graph")
    void createBasicPatternsTest() {

        patterns = patternService.createPatterns(testPatternSettingsBuilder.patternType(PatternType.BASIC));

        assertFalse(patterns.isEmpty());
        assertEquals(mockData.getTestGraphLength() / testPatternSettingsBuilder.build().getLength(), patterns.size());
    }

    @Test
    @DisplayName("[UNIT] Create light basic patterns from mock graph")
    void createLightBasicPatternsTest() {

        patterns = patternService.createPatterns(testPatternSettingsBuilder.patternType(PatternType.LIGHT_BASIC));

        assertFalse(patterns.isEmpty());
        assertEquals(mockData.getTestGraphLength() / testPatternSettingsBuilder.build().getLength(), patterns.size());
    }

    @Test
    @DisplayName("[UNIT] Create predictive patterns from mock graph")
    void createPredictivePatternsTest() {

        patterns = patternService.createPatterns(testPatternSettingsBuilder.patternType(PatternType.PREDICTIVE));

        assertFalse(patterns.isEmpty());
        assertEquals(mockData.getTestGraphLength() / testPatternSettingsBuilder.build().getLength(), patterns.size());
    }

    @Test
    @DisplayName("[UNIT] Create light predictive patterns from mock graph")
    void createLightPredictivePatternsTest() {

        patterns = patternService.createPatterns(testPatternSettingsBuilder.patternType(PatternType.LIGHT_PREDICTIVE));

        assertFalse(patterns.isEmpty());
        assertEquals(mockData.getTestGraphLength() / testPatternSettingsBuilder.build().getLength(), patterns.size());
    }

    @Test
    @DisplayName("[IT] Compute patterns from created predictive patterns")
    void computeBasicIterationTest() {

        patterns = patternService.createPatterns(testPatternSettingsBuilder.patternType(PatternType.PREDICTIVE));
        patterns = patternService.computePatterns(testComputationSettingsBuilder
                .patterns(patterns)
                .computationType(ComputationType.BASIC_ITERATION)
        );

        assertFalse(patterns.isEmpty());

/*        for (Pattern pattern : patterns) {
            assertEquals(1, ((PredictivePattern) pattern).getComputationsHistory().size());
            assertEquals(mockData.getMockGraphDay1().getFloatCandles().size() - pattern.getLength() - ((PredictivePattern) pattern).getScope() + 1,
                    ((PredictivePattern) pattern).getComputationsHistory().get(0).computations());
        }*/
    }

    @Test
    @DisplayName("[IT] Creates and computes pattern boxes from mock graphs")
    void createAndComputePatternBoxes() {

        assertTrue(patternService.createPatternBoxes(coreData, new PatternSettings.Builder().autoconfig(PatternSettings.Autoconfig.TEST)));
        assertEquals(mockData.getNumberOfDifferentMockTimeframes(), coreData.getPatternBoxes().size());
        assertTrue(patternService.computePatternBoxes(coreData, new ComputationSettings.Builder().autoconfig(ComputationSettings.Autoconfig.TEST)));
        assertEquals(mockData.getNumberOfDifferentMockTimeframes(), coreData.getPatternBoxes().size());
    }
}