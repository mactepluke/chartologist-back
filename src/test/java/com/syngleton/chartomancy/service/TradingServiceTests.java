package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.analytics.ComputationSettings;
import com.syngleton.chartomancy.analytics.ComputationType;
import com.syngleton.chartomancy.configuration.DataConfigTest;
import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.factory.PatternSettings;
import com.syngleton.chartomancy.model.charting.misc.Graph;
import com.syngleton.chartomancy.model.charting.misc.Symbol;
import com.syngleton.chartomancy.model.charting.misc.Timeframe;
import com.syngleton.chartomancy.model.charting.patterns.PatternType;
import com.syngleton.chartomancy.model.trading.Trade;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
class TradingServiceTests {

    @Value("${test_data_folder_name}")
    private String testDataFolderName;
    @Value("#{'${test_data_files_names}'.split(',')}")
    private List<String> testDataFilesNames;

    @Autowired
    TradingService tradingService;
    @Autowired
    CoreData coreData;
    @Autowired
    ConfigService configService;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING TRADING SERVICE TESTS ***");

        testDataFolderName = "src/test/resources/" + testDataFolderName;

        coreData = configService.initializeCoreData(
                testDataFolderName,
                testDataFilesNames,
                true,
                true,
                false,
                false,
                false,
                false,
                PatternSettings.Autoconfig.TIMEFRAME,
                ComputationSettings.Autoconfig.TEST,
                ComputationType.BASIC_ITERATION,
                PatternType.LIGHT_PREDICTIVE,
                true,
                true
        );
    }

    @AfterAll
    void tearDown() {
        coreData = null;
        log.info("*** ENDING TRADING SERVICE TESTS ***");
    }

    @Test
    @DisplayName("[IT] Generates optimal basic trade")
    void generateOptimalBasicTradeTest() {

        Graph graph = coreData.getGraph(Symbol.BTC_USD, Timeframe.DAY);

        for (var i = 1; i <1000; i++) {
            Trade trade = tradingService.generateOptimalBasicTrade(graph, coreData, 30 * i, -1, 0);
            if (trade == null)    {
                break;
            }
            log.debug("TRADE# {} -------> {}", i + 1,  trade);
        }
    }

}
