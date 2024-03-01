package co.syngleton.chartomancer.api_requesting;


import co.syngleton.chartomancer.charting.GraphSlicer;
import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.configuration.MockCoreData;
import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.trading.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@ContextConfiguration(classes = {MockCoreData.class})
class BacktestingQueryServiceTests {

    private static final double INITIAL_BALANCE = 10000;

    @Autowired
    private BacktestingQueryService queryService;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING BACKTESTING QUERY SERVICE TESTS ***");
    }

    @AfterAll
    void tearDown() {
        log.info("*** MICROSERVICE BACKTESTING QUERY SERVICE TESTS FINISHED ***");
    }


    @Test
    @DisplayName("[IT] BacktestingQueryService is implemented")
    void backtestingQueryServiceDefaultImplementationTest() {

        final BacktestingResultsDTO result = queryService.getTradingSimulation(
                Symbol.BTC_USD,
                Timeframe.FOUR_HOUR,
                LocalDate.of(2023, 8, 28),
                LocalDate.of(2024, 2, 15),
                INITIAL_BALANCE);

        assertNotEquals(0, result.tradeNumber());
        assertEquals(7362.5, result.pnl());
        assertEquals(1.52, result.profitFactor());
        assertEquals(156.83, result.totalDurationInDays());
        assertEquals(167.97, result.annualizedReturnPercentage());
    }

}