package co.syngleton.chartomancer.service;

import co.syngleton.chartomancer.analytics.computation.ComputationSettings;
import co.syngleton.chartomancer.analytics.computation.ComputationType;
import co.syngleton.chartomancer.analytics.data.CoreData;
import co.syngleton.chartomancer.analytics.factory.PatternSettings;
import co.syngleton.chartomancer.analytics.misc.PurgeOption;
import co.syngleton.chartomancer.analytics.service.InitialiationService;
import co.syngleton.chartomancer.configuration.DataConfigTest;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
@ActiveProfiles("test")
class CoreInitialiationServiceTests {

    private final static int NUMBER_OF_DIFFERENT_MOCK_TIMEFRAMES = 2;
    @Autowired
    CoreData coreData;
    @Autowired
    InitialiationService dataConfigService;
    @Value("${test_data_folder_name}")
    private String testDataFolderName;
    @Value("#{'${test_data_files_names}'.split(',')}")
    private List<String> testDataFilesNames;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING AUTOMATION SERVICE TESTS ***");

        testDataFolderName = "src/test/resources/" + testDataFolderName;
    }

    @AfterAll
    void tearDown() {
        coreData = null;
        log.info("*** ENDING AUTOMATION SERVICE TESTS ***");
    }

    @Test
    @DisplayName("[IT] Generate app data")
    void initializeCoreDataTest() {

        coreData = dataConfigService.initializeCoreData(
                testDataFolderName,
                testDataFilesNames,
                true,
                true,
                true,
                false,
                false,
                false,
                false,
                PurgeOption.NO,
                PatternSettings.Autoconfig.TEST,
                ComputationSettings.Autoconfig.TEST,
                ComputationType.BASIC_ITERATION,
                PatternSettings.PatternType.PREDICTIVE,
                false,
                false,
                false
        );
        assertNotNull(coreData.getTradingPatternBoxes());
        assertEquals(NUMBER_OF_DIFFERENT_MOCK_TIMEFRAMES + 2, coreData.getGraphs().size());
        assertFalse(coreData.getPatternBoxes().isEmpty());
        assertEquals(coreData.getPatternBoxes().size(), coreData.getTradingPatternBoxes().size());
    }
}
