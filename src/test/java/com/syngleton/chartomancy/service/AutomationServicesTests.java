package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.DataConfigTest;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
class AutomationServicesTests {

    @BeforeAll
    void setUp() {
        log.info("*** STARTING AUTOMATION SERVICE TESTS ***");
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING AUTOMATION SERVICE TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Generate app data")
    void generateAppDataTest() {
        // TODO Implement this test
        Assertions.fail("Test not implemented yet/");
    }

}
