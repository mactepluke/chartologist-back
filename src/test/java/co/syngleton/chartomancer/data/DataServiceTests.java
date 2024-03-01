package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.configuration.MockConfig;
import co.syngleton.chartomancer.configuration.MockData;
import co.syngleton.chartomancer.configuration.MockDataConfig;
import co.syngleton.chartomancer.core_entities.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {MockConfig.class, MockDataConfig.class})
@ActiveProfiles("test")
class DataServiceTests {

    public static final String TEST_CORE_DATA_FILE_PATH = "./core_data/TEST_coredata.ser";
    @Autowired
    DataProcessor dataProcessor;
    @Autowired
    MockData mockData;
    CoreData coreData;
    @Value("${data.folder_name}")
    private String testDataFolderName;
    private String getTestDataFolderPath;
    @Value("#{'${data.files_names}'.split(',')}")
    private List<String> testDataFilesNames;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING DATA PROCESSOR TESTS ***");

        coreData = DefaultCoreData.newInstance();
        mockData.getTestGraphs().forEach(graph -> coreData.addGraph(graph));

        List<Pattern> patterns = new ArrayList<>();
        BasicPattern basicPattern = new BasicPattern(
                new ArrayList<>(),
                10,
                mockData.getMockGraphDay1().getSymbol(),
                mockData.getMockGraphDay1().getTimeframe());
        patterns.add(new ComputablePattern(basicPattern, 5));

        coreData.addPatterns(patterns);

        getTestDataFolderPath = "./" + testDataFolderName;
    }

    @AfterAll
    void tearDown() {
        coreData = null;
        mockData.resetGraphs();
        log.info("*** ENDING DATA PROCESSOR TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Loads all test files and creates their graphs")
    void loadGraphsTest() {
        CoreData testCoreData = DefaultCoreData.newInstance();

        assertTrue(dataProcessor.loadGraphs(testCoreData, getTestDataFolderPath, testDataFilesNames));
        assertEquals(testDataFilesNames.size(), testCoreData.getReadOnlyGraphs().size());
    }

    @Test
    @DisplayName("[UNIT] Loads trading data from file")
    void loadTradingDataTest() {
        assertTrue(dataProcessor.loadCoreData(coreData, TEST_CORE_DATA_FILE_PATH));
    }

    @Test
    @DisplayName("[UNIT] Saves trading data to file")
    void saveTradingDataTest() {
        assertTrue(dataProcessor.saveCoreData(coreData, "./core_data/trashdata.ser"));
    }

    @Test
    @DisplayName("[UNIT] Creates graphs for missing timeframes")
    void createGraphsForMissingTimeframesTest() {
        assertTrue(dataProcessor.createGraphsForMissingTimeframes(coreData));
        assertEquals(mockData.getNumberOfDifferentMockTimeframes() + 2, coreData.getReadOnlyGraphs().size());
    }
}
