package co.syngleton.chartomancer.automation;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.DefaultCoreData;
import co.syngleton.chartomancer.core_entities.Graph;
import co.syngleton.chartomancer.core_entities.PurgeOption;
import co.syngleton.chartomancer.data.DataProcessor;
import co.syngleton.chartomancer.trading.TradeGenerator;
import co.syngleton.chartomancer.trading.TradeSimulator;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class TradeGeneratorTests {

    public static final String TEST_CORE_DATA_FILE_PATH = "./core_data/TEST_coredata.ser";
    private static final String TEST_PATH = "./src/test/resources/";
    private static final double INITIAL_BALANCE = 10000;
    private static final double MINIMUM_BALANCE = 5000;
    private static final int MAX_TRADES = 100;
    private static final int EXPECTED_BALANCE_X = 2;
    private static final boolean WRITE_REPORTS = true;
    private static final String EXPECTED_REPORT = "*** ADVANCED DUMMY TRADE RESULTS ***\n" +
            "Result: NEUTRAL\n" +
            "Number of dummy trades performed: 24\n" +
            "Number of longs: 20\n" +
            "Number of shorts: 4\n" +
            "Number of useless trades: 280\n" +
            "Used to Useless trade ratio: 0.09\n" +
            "Initial balance: $ 10000.0\n" +
            "Target balance amount: $ 20000.0\n" +
            "Final Account Balance: $ 11145.01\n" +
            "Total duration (in days): 62.0\n" +
            "Annualized return %: 63.16\n" +
            "\n" +
            "*********************     All | Longs | Shorts\n" +
            "Total PnL                  $ 1145.01 | $ 1267.29 | $ -122.28\n" +
            "Total Return               11.41% | 11.81% | -0.4%\n" +
            "Avg. return per trade      0.48% | 0.59% | -0.1%\n" +
            "Total # of trades          24 | 20 | 4\n" +
            "Batting avg.               75.0% | 75.0% | 75.0%\n" +
            "Win/Loss ratio             3.0 | 3.0 | 3.0\n" +
            "Average PnL                $ 47.71 | $ 63.36 | $ -30.57\n" +
            "** Profit factor: 1.66, qualification: GOOD";
    @Autowired
    TradeGenerator tradeGenerator;
    @Autowired
    TradeSimulator tradeSimulator;
    CoreData coreData;
    @Autowired
    DataProcessor dataProcessor;
    @Value("${data.folder_name}")
    private String testDataFolderName;
    @Value("#{'${automation.dummy_graphs_data_files_names}'.split(',')}")
    private List<String> testDummyGraphsDataFilesNames;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING TRADING SERVICE TESTS ***");
        coreData = DefaultCoreData.newInstance();
        dataProcessor.loadCoreData(coreData, TEST_CORE_DATA_FILE_PATH);
        coreData.pushTradingPatternData();
        coreData.purgeUselessData(PurgeOption.GRAPHS_AND_PATTERNS);
        dataProcessor.loadGraphs(coreData, TEST_PATH + testDataFolderName + "/", testDummyGraphsDataFilesNames);
        dataProcessor.createGraphsForMissingTimeframes(coreData);
    }

    @AfterAll
    void tearDown() {
        coreData = null;
        log.info("*** ENDING TRADING SERVICE TESTS ***");
    }

    @Test
    @Disabled
    @DisplayName("[IT] Checks test core data integrity")
    void checkCoreDataIntegrity() {
        dataProcessor.printCoreData(coreData);

        assertEquals(404, coreData.getTradingPatterns(Symbol.BTC_USD, Timeframe.FOUR_HOUR, 1).size());
        assertEquals(855, coreData.getTradingPatterns(Symbol.BTC_USD, Timeframe.FOUR_HOUR, 2).size());
        assertEquals(1302, coreData.getTradingPatterns(Symbol.BTC_USD, Timeframe.FOUR_HOUR, 3).size());
        assertEquals(1696, coreData.getTradingPatterns(Symbol.BTC_USD, Timeframe.FOUR_HOUR, 4).size());
        assertEquals(2076, coreData.getTradingPatterns(Symbol.BTC_USD, Timeframe.FOUR_HOUR, 5).size());
        assertEquals(2450, coreData.getTradingPatterns(Symbol.BTC_USD, Timeframe.FOUR_HOUR, 6).size());
        assertEquals(2708, coreData.getTradingPatterns(Symbol.BTC_USD, Timeframe.FOUR_HOUR, 7).size());
        assertEquals(2952, coreData.getTradingPatterns(Symbol.BTC_USD, Timeframe.FOUR_HOUR, 8).size());
        assertEquals(3134, coreData.getTradingPatterns(Symbol.BTC_USD, Timeframe.FOUR_HOUR, 9).size());
        assertEquals(3332, coreData.getTradingPatterns(Symbol.BTC_USD, Timeframe.FOUR_HOUR, 10).size());
        assertEquals(3488, coreData.getTradingPatterns(Symbol.BTC_USD, Timeframe.FOUR_HOUR, 11).size());
        assertEquals(3683, coreData.getTradingPatterns(Symbol.BTC_USD, Timeframe.FOUR_HOUR, 12).size());
    }

    @Test
    @Disabled
    @DisplayName("[IT] Tests if dummy trades results are correct")
    void dummyTradesTest() {

        DummyTradesManager dtm = new DummyTradesManager(
                INITIAL_BALANCE,
                MINIMUM_BALANCE,
                EXPECTED_BALANCE_X,
                MAX_TRADES,
                tradeGenerator,
                tradeSimulator,
                coreData,
                WRITE_REPORTS,
                new DummyTradesSummaryTable("testTable"),
                TEST_PATH,
                dataProcessor);

        Graph graph = coreData.getGraph(Symbol.BTC_USD, Timeframe.FOUR_HOUR);

        String reportLog = "";

        reportLog = dtm.launchDummyTrades(graph, coreData, false, reportLog);

        log.info(reportLog);
        assertTrue(reportLog.contains(EXPECTED_REPORT));
    }

}
