package co.syngleton.chartomancer.api_controller;

import co.syngleton.chartomancer.api_requesting.BacktestingQueryService;
import co.syngleton.chartomancer.api_requesting.BacktestingResultsDTO;
import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.trading.TradingAccount;
import co.syngleton.chartomancer.trading.TradingSimulationDefaultResult;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class BacktestingControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BacktestingQueryService queryService;

    private BacktestingResultsDTO results;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING BACKTESTING ENDPOINTS TESTS ***");

        results = BacktestingResultsDTO.from(TradingSimulationDefaultResult.generateFrom(new TradingAccount(), 10000, Symbol.BTC_USD, Timeframe.DAY, 100));

        when(queryService.getTradingSimulation(any(), any(), any(), any(), anyFloat())).thenReturn(results);
    }

    @AfterAll
    void tearDown() {
        log.info("*** MICROSERVICE BACKTESTING ENDPOINTS TESTS FINISHED ***");
    }


    @Test
    @DisplayName("[UNIT] Endpoint 'get-results' is accessible")
    void getBacktestingResultsTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/backtesting/get-results")
                        .param("symbol", "BTC_USD")
                        .param("timeframe", "DAY")
                        .param("startDate", "2021-11-20")
                        .param("endDate", "2023-11-20")
                        .param("accountBalance", "10000"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[UNIT] Endpoint 'get-results' fails if date is of wrong format")
    void getBacktestingResultsWithInvalidDateFormatTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/backtesting/get-results")
                        .param("symbol", "BTC_USD")
                        .param("timeframe", "DAY")
                        .param("startDate", "2021-11-10")
                        .param("endDate", "2023-13-11")
                        .param("accountBalance", "10000"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[UNIT] Endpoint 'get-results' fails if account balance is of wrong format")
    void getBacktestingResultsWithInvalidAccountBalanceTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/backtesting/get-results")
                        .param("symbol", "BTC_USD")
                        .param("timeframe", "DAY")
                        .param("startDate", "2021-11-10")
                        .param("endDate", "2023-11-11")
                        .param("accountBalance", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[UNIT] Endpoint 'get-results' fails if start date is after end date")
    void getBacktestingResultsWithInvalidDatePeriodTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/backtesting/get-results")
                        .param("symbol", "BTC_USD")
                        .param("timeframe", "DAY")
                        .param("startDate", "2023-11-10")
                        .param("endDate", "2021-11-11")
                        .param("accountBalance", "1000"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[UNIT] Endpoint 'get-results' gets no results from the provider")
    void getBacktestingResultsWithResultsNotGenerated() throws Exception {

        when(queryService.getTradingSimulation(any(), any(), any(), any(), anyFloat())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/backtesting/get-results")
                        .param("symbol", "BTC_USD")
                        .param("timeframe", "DAY")
                        .param("startDate", "2021-11-10")
                        .param("endDate", "2023-11-11")
                        .param("accountBalance", "100"))
                .andExpect(status().isConflict());
    }
}
