package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.DataConfigTest;
import com.syngleton.chartomancy.MockData;
import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.model.charting.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
class DataServiceTests {

    @Value("${test_data_folder_name}")
    private String testDataFolderName;
    private String getTestDataFolderPath;
    @Value("#{'${test_data_files_names}'.split(',')}")
    private List<String> testDataFilesNames;

    @Autowired
    DataService dataService;
    @Autowired
    CoreData coreData;
    @Autowired
    MockData mockData;

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
        log.info("*** ENDING DATA SERVICE TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Loads all test files and creates their graphs")
    void loadGraphsTest() {
        CoreData testCoreData = new CoreData();

        assertTrue(dataService.loadGraphs(testCoreData, getTestDataFolderPath, testDataFilesNames));
        assertEquals(testDataFilesNames.size(), testCoreData.getGraphs().size());
    }

    @Test
    @DisplayName("[UNIT] Loads trading data from file")
    void loadTradingDataTest() {
        assertTrue(dataService.loadTradingData(coreData));
    }

    @Test
    @DisplayName("[UNIT] Saves trading data to file")
    void saveTradingDataTest() {
        assertTrue(dataService.saveTradingData(coreData));
    }

    //TODO vérifier pourquoi ceci n'est pas vrai dans les faits
    @Test
    @DisplayName("[UNIT] Generates trading data")
    void generateTradingDataTest() {
        assertTrue(dataService.generateTradingData(coreData));
        assertEquals(coreData.getPatternBoxes().size(), coreData.getTradingPatternBoxes().size());
    }

    @Test
    @DisplayName("[UNIT] Purges non-trading data")
    void purgeNonTradingDataTest() {
        assertTrue(dataService.purgeNonTradingData(coreData));
    }

    @Test
    @DisplayName("[UNIT] Creates graphs for missing timeframes")
    void createGraphsForMissingTimeframesTest() {
        assertTrue(dataService.createGraphsForMissingTimeframes(coreData));
        assertEquals(mockData.getNumberOfDifferentMockTimeframes(), mockData.getNumberOfDifferentMockTimeframes() + 1);
    }
}
