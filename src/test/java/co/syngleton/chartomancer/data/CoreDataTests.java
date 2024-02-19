package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.configuration.MockConfig;
import co.syngleton.chartomancer.configuration.MockData;
import co.syngleton.chartomancer.configuration.MockDataConfig;
import co.syngleton.chartomancer.core_entities.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {MockConfig.class, MockDataConfig.class})
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
                mockData.getMockGraphDay1().getSymbol(),
                mockData.getMockGraphDay1().getTimeframe());
        patterns.add(new ComputablePattern(basicPattern, 5));

        coreData.addPatterns(patterns);
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

    @Test
    @DisplayName("[UNIT] Tests")
    void getTradingPatternLengthTest() {
        CoreData coreData = DefaultCoreData.newInstance();

        BasicPattern basicPattern = new BasicPattern(
                mockData.getIntCandles(),
                100,
                Symbol.BTC_USD,
                Timeframe.FOUR_HOUR
        );

        ComputablePattern computablePattern = new ComputablePattern(basicPattern, 12);

        coreData.addPatterns(List.of(computablePattern, computablePattern, computablePattern));
        coreData.pushTradingPatternData();

        log.debug(coreData);

        assertEquals(mockData.getIntCandles().size(), coreData.getTradingPatternLength(Symbol.BTC_USD, Timeframe.FOUR_HOUR));
    }
}

