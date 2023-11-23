package co.syngleton.chartomancer.analytics;

import co.syngleton.chartomancer.analytics.computation.ComputationSettings;
import co.syngleton.chartomancer.analytics.data.CoreData;
import co.syngleton.chartomancer.analytics.factory.PatternSettings;
import co.syngleton.chartomancer.analytics.model.Pattern;
import co.syngleton.chartomancer.analytics.service.CoreDataService;
import co.syngleton.chartomancer.analytics.service.PatternComputingService;
import co.syngleton.chartomancer.configuration.DataConfigTest;
import co.syngleton.chartomancer.configuration.MockData;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
@ActiveProfiles("test")
class PatternComputingServiceTests {

    @Autowired
    PatternComputingService patternComputingService;
    @Autowired
    MockData mockData;
    @Autowired
    CoreData coreData;
    @Autowired
    CoreDataService coreDataService;
    private List<Pattern> patterns;
    private PatternSettings.Builder testPatternSettingsBuilder;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING PATTERN SERVICE TESTS ***");

        coreData.setGraphs(mockData.getTestGraphs());

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

        patterns = patternComputingService.createPatterns(testPatternSettingsBuilder.patternType(PatternSettings.PatternType.BASIC));

        assertFalse(patterns.isEmpty());
        assertEquals(mockData.getTestGraphLength() / testPatternSettingsBuilder.build().getLength(), patterns.size());
    }

    @Test
    @DisplayName("[UNIT] Create light predictive patterns from mock graph")
    void createPredictivePatternsTest() {

        patterns = patternComputingService.createPatterns(testPatternSettingsBuilder.patternType(PatternSettings.PatternType.PREDICTIVE));

        assertFalse(patterns.isEmpty());
        assertEquals(mockData.getTestGraphLength() / testPatternSettingsBuilder.build().getLength(), patterns.size());
    }

    @Test
    @DisplayName("[IT] Creates and computes pattern boxes from mock graphs")
    void createAndComputePatternBoxes() {

        assertTrue(patternComputingService.createPatternBoxes(coreData, new PatternSettings.Builder().autoconfig(PatternSettings.Autoconfig.TEST)));
        assertEquals(mockData.getNumberOfDifferentMockTimeframes(), coreData.getPatternBoxes().size());
        assertTrue(patternComputingService.computePatternBoxes(coreData, new ComputationSettings.Builder().autoconfig(ComputationSettings.Autoconfig.TEST)));
        assertEquals(mockData.getNumberOfDifferentMockTimeframes(), coreData.getPatternBoxes().size());
    }
}
