package com.syngleton.chartomancy.controller;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DevtoolsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING DEVTOOLS CONTROLLER TESTS ***");
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING DEVTOOLS CONTROLLER TESTS ***");
    }

    @Test
    @DisplayName("Launch shell endpoint")
    void load() throws Exception {
        mockMvc.perform(get("/devtools/launch-shell"))
                .andExpect(status().isOk());
    }
}
