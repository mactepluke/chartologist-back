package co.syngleton.chartomancer.config_controller;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class ConfigControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING CONFIG ENDPOINTS TESTS ***");
    }

    @AfterAll
    void tearDown() {
        log.info("*** MICROSERVICE CONFIG ENDPOINTS TESTS FINISHED ***");
    }

    @Test
    @DisplayName("[UNIT] Endpoint '/config/get-user-validation-data' is accessible")
    void getUserValidationDataTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/config/get-user-validation-data"))
                .andExpect(status().isOk());
    }
}
