package co.syngleton.chartomancer.service;

import co.syngleton.chartomancer.configuration.DataConfigTest;
import co.syngleton.chartomancer.configuration.MockData;
import co.syngleton.chartomancer.data.CoreData;
import co.syngleton.chartomancer.model.charting.misc.PatternBox;
import co.syngleton.chartomancer.model.charting.patterns.Pattern;
import co.syngleton.chartomancer.model.charting.patterns.pixelated.BasicPattern;
import co.syngleton.chartomancer.model.charting.patterns.pixelated.PredictivePattern;
import co.syngleton.chartomancer.service.domain.DataService;
import co.syngleton.chartomancer.service.misc.PurgeOption;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
@ActiveProfiles("test")
class DataServiceTests {

    @Autowired
    DataService dataService;
    @Autowired
    MockData mockData;
    @Autowired
    CoreData coreData;
    @Value("${test_data_folder_name}")
    private String testDataFolderName;
    private String getTestDataFolderPath;
    @Value("#{'${test_data_files_names}'.split(',')}")
    private List<String> testDataFilesNames;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING DATA SERVICE TESTS ***");

        coreData.setGraphs(mockData.getTestGraphs());
        coreData.setPatternBoxes(new HashSet<>());
        List<Pattern> patterns = new ArrayList<>();
        BasicPattern basicPattern = new BasicPattern(
                new ArrayList<>(),
                10,
                10,
                mockData.getMockGraphDay1().getSymbol(),
                mockData.getMockGraphDay1().getTimeframe(),
                LocalDateTime.now());
        patterns.add(new PredictivePattern(basicPattern, 5));
        PatternBox patternBox = new PatternBox(
                mockData.getMockGraphDay1(),
                patterns
        );
        coreData.getPatternBoxes().add(patternBox);
        getTestDataFolderPath = "src/test/resources/" + testDataFolderName;
    }

    @AfterAll
    void tearDown() {
        coreData = null;
        mockData.resetGraphs();
        log.info("*** ENDING DATA SERVICE TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Loads all test files and creates their graphs")
    void loadGraphsTest() {
        CoreData testCoreData = new CoreData();

        assertTrue(dataService.loadGraphs(testCoreData, getTestDataFolderPath, testDataFilesNames));
        assertEquals(testDataFilesNames.size(), testCoreData.getGraphs().size());
    }

    @Disabled
    @Test
    @DisplayName("[UNIT] Loads trading data from file")
    void loadTradingDataTest() {
        assertTrue(dataService.loadCoreData(coreData));
    }

    @Disabled
    @Test
    @DisplayName("[UNIT] Saves trading data to file")
    void saveTradingDataTest() {
        assertTrue(dataService.saveCoreData(coreData));
    }

    @Test
    @DisplayName("[UNIT] Generates trading data")
    void generateTradingDataTest() {
        assertTrue(dataService.generateTradingData(coreData));
        assertEquals(coreData.getPatternBoxes().size(), coreData.getTradingPatternBoxes().size());
    }

    @Test
    @DisplayName("[UNIT] Purges non-trading data")
    void purgeNonTradingDataTest() {
        assertTrue(dataService.purgeNonTradingData(coreData, PurgeOption.GRAPHS_AND_PATTERNS));
    }

    @Test
    @DisplayName("[UNIT] Creates graphs for missing timeframes")
    void createGraphsForMissingTimeframesTest() {
        assertTrue(dataService.createGraphsForMissingTimeframes(coreData));
        assertEquals(mockData.getNumberOfDifferentMockTimeframes() + 2, coreData.getGraphs().size());
    }
}
