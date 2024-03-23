package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.configuration.MockCoreData;
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
@ContextConfiguration(classes = {TradingConfig.class, MockCoreData.class})
@ActiveProfiles("test")
class AutomationTradingServiceTests {
    private static final double INITIAL_BALANCE = 10000;
    private static final double MINIMUM_BALANCE = 5000;
    private static final int MAX_TRADES = 100;
    private static final int EXPECTED_BALANCE_X = 2;
    @Autowired
    private AutomationTradingService automationTradingService;
    @Autowired
    private CoreData coreData;
    private TradingConditionsChecker checker;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING TRADING SERVICE TESTS ***");

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

        TradingSimulationResult result = automationTradingService.simulateTrades(TradeSimulationStrategy.randomize(graph, coreData, tradingAccount), checker);

        assertNotEquals(0, result.account().getNumberOfTrades());
    }

    @Test
    @DisplayName("[UNIT] Tests if deterministic dummy trades results are correct")
    void deterministicDummyTradesTest() {

        Graph graph = coreData.getGraph(Symbol.BTC_USD, Timeframe.FOUR_HOUR);

        TradingAccount tradingAccount = new TradingAccount();
        tradingAccount.credit(INITIAL_BALANCE);

        TradingSimulationResult result = automationTradingService.simulateTrades(TradeSimulationStrategy.iterate(graph, coreData, tradingAccount), checker);

        assertNotEquals(0, result.account().getNumberOfTrades());
        assertEquals(7362.5, result.account().getTotalPnl());
        assertEquals(1.52, result.account().getProfitFactor());
        assertEquals(156.83, result.totalDurationInDays());
        assertEquals(167.97, result.annualizedReturnPercentage());
    }

}