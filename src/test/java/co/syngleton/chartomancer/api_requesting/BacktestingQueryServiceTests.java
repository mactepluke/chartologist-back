package co.syngleton.chartomancer.api_requesting;


import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.trading.DefaultTradingSimulationResult;
import co.syngleton.chartomancer.trading.RequestingTradingService;
import co.syngleton.chartomancer.trading.TradingAccount;
import co.syngleton.chartomancer.trading.TradingSimulationResult;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class BacktestingQueryServiceTests {

    @Autowired
    private BacktestingQueryService queryService;
    @Mock
    private RequestingTradingService requestingTradingService;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING BACKTESTING QUERY SERVICE TESTS ***");

        /*final TradingConditionsChecker checker = TradingConditionsChecker.builder()
                .maxTrades(100)
                .maxBlankTrades(1000)
                .maximumAccountBalance(10000)
                .minimumAccountBalance(5000)
                .build();*/

        //final TradeSimulationStrategy strategy = TradeSimulationStrategy.iterate(new Graph(any(), any(), any(), any()), coreData, any());

    }

    @AfterAll
    void tearDown() {
        log.info("*** MICROSERVICE ACKTESTING QUERY SERVICE TESTS FINISHED ***");
    }


    @Test
    @DisplayName("[UNIT] BacktestingQueryService is implemented")
    void backtestingQueryServiceBasicImplementationTest() {

        final TradingAccount tradingAccount = new TradingAccount();
        tradingAccount.credit(10000);

        final TradingSimulationResult tradingSimulationResult = DefaultTradingSimulationResult.generateFrom(
                tradingAccount,
                5000,
                Symbol.BTC_USD,
                Timeframe.DAY,
                10);

        when(requestingTradingService.simulateTrades(any(), any()))
                .thenReturn(tradingSimulationResult);

        final BacktestingResultsDTO results = queryService.getTradingSimulation(any(), any(), any(), any(), 1000);

        assertEquals(10000, results.accountBalance());
        assertEquals(0, results.tradeNumber());
        assertEquals(0, results.trades().size());
        assertEquals(0, results.actualTradingDurationPercentage());
    }

}