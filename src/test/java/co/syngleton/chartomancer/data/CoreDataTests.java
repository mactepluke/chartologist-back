package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.core_entities.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
@ActiveProfiles("test")
class CoreDataTests {


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
        log.info("*** STARTING CORE DATA TESTS ***");

        coreData = DefaultCoreData.newInstance();
        mockData.getTestGraphs().forEach(graph -> coreData.addGraph(graph));
        List<Pattern> patterns = new ArrayList<>();
        BasicPattern basicPattern = new BasicPattern(
                new ArrayList<>(),
                10,
                10,
                mockData.getMockGraphDay1().getSymbol(),
                mockData.getMockGraphDay1().getTimeframe(),
                LocalDateTime.now());
        patterns.add(new PredictivePattern(basicPattern, 5));

        coreData.addPatterns(patterns, mockData.getMockGraphDay1().getSymbol(), mockData.getMockGraphDay1().getTimeframe());
        getTestDataFolderPath = "src/test/resources/" + testDataFolderName;
    }

    @AfterAll
    void tearDown() {
        coreData = null;
        mockData.resetGraphs();
        log.info("*** ENDING CORE DATA TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Generates trading data")
    void generateTradingDataTest() {
        assertTrue(coreData.pushTradingPatternData());
        assertEquals(coreData.getNumberOfPatternSets(), coreData.getNumberOfTradingPatternSets());
    }

    @Test
    @DisplayName("[UNIT] Purges non-trading data")
    void purgeNonTradingDataTest() {
        assertTrue(coreData.purgeUselessData(PurgeOption.GRAPHS_AND_PATTERNS));
        assertTrue(coreData.purgeUselessData(PurgeOption.GRAPHS_AND_PATTERNS));
    }
}

