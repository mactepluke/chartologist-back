package co.syngleton.chartomancer.service;

import co.syngleton.chartomancer.data.CoreData;
import co.syngleton.chartomancer.analytics.ComputationSettings;
import co.syngleton.chartomancer.analytics.ComputationType;
import co.syngleton.chartomancer.configuration.DataConfigTest;
import co.syngleton.chartomancer.factory.PatternSettings;
import co.syngleton.chartomancer.model.charting.misc.PatternType;
import co.syngleton.chartomancer.service.domain.TradingService;
import co.syngleton.chartomancer.service.misc.ConfigService;
import co.syngleton.chartomancer.service.misc.PurgeOption;
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
                PurgeOption.NO,
                PatternSettings.Autoconfig.TIMEFRAME_LONG,
                ComputationSettings.Autoconfig.TEST,
                ComputationType.BASIC_ITERATION,
                PatternType.LIGHT_PREDICTIVE,
                true,
                true,
                true
        );
    }

    @AfterAll
    void tearDown() {
        coreData = null;
        log.info("*** ENDING TRADING SERVICE TESTS ***");
    }

    @Disabled
    @Test
    @DisplayName("[IT] Generates optimal basic trade")
    void generateOptimalBasicTradeTest() {
    }

}
