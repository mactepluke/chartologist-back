package com.syngleton.chartomancy.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataServiceTests {

    private final static String TEST_DATA_FILE_PATH = "src/test/resources/testdata/Bitfinex_BTCUSD_TEST.csv";

    @Autowired
    DataService dataService;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING DATA SERVICE TESTS ***");
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING DATA SERVICE TESTS ***");
    }
//TODO Implement test
    @Test
    @DisplayName("Loads test file.")
    void load() {
        dataService.load(TEST_DATA_FILE_PATH);
        fail();
    }
}
