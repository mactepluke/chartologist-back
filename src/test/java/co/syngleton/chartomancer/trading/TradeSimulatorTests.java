package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.DefaultCoreData;
import co.syngleton.chartomancer.core_entities.Graph;
import co.syngleton.chartomancer.core_entities.PurgeOption;
import co.syngleton.chartomancer.data.DataProcessor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {TradingConfig.class})
@ActiveProfiles("test")
class TradeSimulatorTests {
    public static final String TEST_CORE_DATA_FILE_PATH = "./core_data/TEST_coredata.ser";
    private static final String TEST_PATH = "./src/test/resources/";
    private static final double INITIAL_BALANCE = 10000;
    private static final double MINIMUM_BALANCE = 5000;
    private static final int MAX_TRADES = 100;
    private static final int EXPECTED_BALANCE_X = 2;
    @Autowired
    TradeSimulator tradeSimulator;
    CoreData coreData;
    @Autowired
    DataProcessor dataProcessor;
    private TradingConditionsChecker checker;
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

        this.checker = TradingConditionsChecker.builder()
                .maxTrades(MAX_TRADES)
                .maxBlankTrades(MAX_TRADES * 10)
                .maximumAccountBalance(INITIAL_BALANCE * EXPECTED_BALANCE_X)
                .minimumAccountBalance(MINIMUM_BALANCE)
                .build();
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING TRADING SERVICE TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Checks test core data integrity")
    void checkCoreDataIntegrity() {
        log.debug(this.coreData);
        assertFalse(this.coreData.hasInvalidStructure());
    }

    @Test
    @DisplayName("[UNIT] Tests if randomized dummy trades results are correct")
    void randomizedDummyTradesTest() {

        Graph graph = coreData.getGraph(Symbol.BTC_USD, Timeframe.FOUR_HOUR);

        TradingAccount tradingAccount = new TradingAccount();
        tradingAccount.credit(INITIAL_BALANCE);

        TradingSimulationResult result = tradeSimulator.simulateTrades(TradeSimulationStrategy.randomize(graph, coreData, tradingAccount), checker);

        assertNotEquals(0, result.account().getNumberOfTrades());
    }

    @Test
    @DisplayName("[UNIT] Tests if deterministic dummy trades results are correct")
    void deterministicDummyTradesTest() {

        Graph graph = coreData.getGraph(Symbol.BTC_USD, Timeframe.FOUR_HOUR);

        TradingAccount tradingAccount = new TradingAccount();
        tradingAccount.credit(INITIAL_BALANCE);

        TradingSimulationResult result = tradeSimulator.simulateTrades(TradeSimulationStrategy.iterate(graph, coreData, tradingAccount), checker);

        assertNotEquals(0, result.account().getNumberOfTrades());
        assertEquals(7362.5, result.account().getTotalPnl());
        assertEquals(1.52, result.account().getProfitFactor());
        assertEquals(156.83, result.totalDurationInDays());
        assertEquals(167.97, result.annualizedReturnPercentage());
    }

}
