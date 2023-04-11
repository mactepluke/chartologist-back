package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.service.data.DataService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataServiceTests {

    @Value("${test_data_file_path}")
    private String testDataFilePath;

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

    @Test
    @DisplayName("Loads test file")
    void load() {
        assertTrue(dataService.load(testDataFilePath));
    }
}
