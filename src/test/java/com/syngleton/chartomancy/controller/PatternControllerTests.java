package com.syngleton.chartomancy.controller;

import com.syngleton.chartomancy.service.patterns.PatternService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatternControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PatternService patternService;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING PATTERN CONTROLLER TESTS ***");
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING PATTERN CONTROLLER TESTS ***");
    }

    @Test
    @DisplayName("Create pattern endpoint")
    void create() throws Exception {

        when(patternService.create()).thenReturn(true);

        mockMvc.perform(get("/pattern/create"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Print patterns endpoint")
    void printPatterns() throws Exception {

        when(patternService.printPatterns()).thenReturn(true);

        mockMvc.perform(get("/pattern/print-patterns"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Print patterns list endpoint")
    void printPatternsList() throws Exception {

        when(patternService.printPatternsList()).thenReturn(true);

        mockMvc.perform(get("/pattern/print-patterns-list"))
                .andExpect(status().isOk());
    }


}
