package co.syngleton.chartomancer.api_controller;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class BacktestingControllerTests {

    @Autowired
    private MockMvc mockMvc;


    @BeforeAll
    void setUp() {
        log.info("*** STARTING DUMMY TRADES ENDPOINTS TESTS ***");
    }

    @AfterAll
    void tearDown() {
        log.info("*** MICROSERVICE DUMMY TRADES ENDPOINTS TESTS FINISHED ***");
    }


}
