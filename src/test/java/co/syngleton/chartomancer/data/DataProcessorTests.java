package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.shared_domain.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
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

import static co.syngleton.chartomancer.shared_constants.Misc.TEST_CORE_DATA_FILENAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
@ActiveProfiles("test")
class DataProcessorTests {

    @Autowired
    DataProcessor dataProcessor;
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
        log.info("*** STARTING DATA PROCESSOR TESTS ***");

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
        CoreData testCoreData = new DefaultCoreData();

        assertTrue(dataProcessor.loadGraphs(testCoreData, getTestDataFolderPath, testDataFilesNames));
        assertEquals(testDataFilesNames.size(), testCoreData.getGraphs().size());
    }

    @Test
    @DisplayName("[UNIT] Loads trading data from file")
    void loadTradingDataTest() {
        assertTrue(dataProcessor.loadCoreData(coreData, TEST_CORE_DATA_FILENAME));
    }

    @Test
    @DisplayName("[UNIT] Saves trading data to file")
    void saveTradingDataTest() {
        assertTrue(dataProcessor.saveCoreData(coreData, "trashdata.ser"));
    }

    @Test
    @DisplayName("[UNIT] Generates trading data")
    void generateTradingDataTest() {
        assertTrue(dataProcessor.generateTradingData(coreData));
        assertEquals(coreData.getPatternBoxes().size(), coreData.getTradingPatternBoxes().size());
    }

    @Test
    @DisplayName("[UNIT] Creates graphs for missing timeframes")
    void createGraphsForMissingTimeframesTest() {
        assertTrue(dataProcessor.createGraphsForMissingTimeframes(coreData));
        assertEquals(mockData.getNumberOfDifferentMockTimeframes() + 2, coreData.getGraphs().size());
    }
}
