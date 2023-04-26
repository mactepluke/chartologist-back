package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.DataConfigTest;
import com.syngleton.chartomancy.analytics.ComputationSettings;
import com.syngleton.chartomancy.analytics.ComputationType;
import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.factory.PatternSettings;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
class ConfigServiceTests {

    @Value("${test_data_folder_name}")
    private String testDataFolderName;
    @Value("#{'${test_data_files_names}'.split(',')}")
    private List<String> testDataFilesNames;

    private final static int NUMBER_OF_DIFFERENT_MOCK_TIMEFRAMES = 2;

    @Autowired
    CoreData coreData;
    @Autowired
    ConfigService configService;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING AUTOMATION SERVICE TESTS ***");
        testDataFolderName = "src/test/resources/" + testDataFolderName;
    }

    @AfterAll
    void tearDown() {
        coreData = null;
        log.info("*** ENDING AUTOMATION SERVICE TESTS ***");
    }

    @Test
    @DisplayName("[IT] Generate app data")
    void initializeCoreDataTest() {

        coreData = configService.initializeCoreData(
                testDataFolderName,
                testDataFilesNames,
                true,
                true,
                false,
                false,
                false,
                false,
                PatternSettings.Autoconfig.TEST,
                ComputationSettings.Autoconfig.TEST,
                ComputationType.BASIC_ITERATION,
                true,
                false
        );
        assertNotNull(coreData.getTradingPatternBoxes());
        assertEquals(NUMBER_OF_DIFFERENT_MOCK_TIMEFRAMES, coreData.getPatternBoxes().size());
        assertEquals(NUMBER_OF_DIFFERENT_MOCK_TIMEFRAMES, coreData.getTradingPatternBoxes().size());
    }

}
