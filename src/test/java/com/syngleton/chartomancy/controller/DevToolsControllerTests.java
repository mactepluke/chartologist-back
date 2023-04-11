package com.syngleton.chartomancy.controller;

import com.syngleton.chartomancy.service.devtools.DevToolsService;
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
class DevToolsControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DevToolsService devToolsService;
    private DataController dataController;
    private PatternController patternController;

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
    void launchShell() throws Exception {

        when(devToolsService.launchShell(dataController, patternController)).thenReturn(true);

        mockMvc.perform(get("/devtools/launch-shell"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Run script endpoint")
    void runScript() throws Exception {

        when(devToolsService.runScript(dataController, patternController)).thenReturn(true);

        mockMvc.perform(get("/devtools/launch-shell"))
                .andExpect(status().isOk());
    }
}
