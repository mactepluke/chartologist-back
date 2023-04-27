package com.syngleton.chartomancy.controller;

import com.syngleton.chartomancy.configuration.DataConfigTest;
import com.syngleton.chartomancy.controller.root.DataController;
import com.syngleton.chartomancy.controller.root.PatternController;
import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.service.ShellService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
class ShellControllerTests {

    private final CoreData coreData = new CoreData();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DataController dataController;
    @Autowired
    private PatternController patternController;
    @MockBean
    private ShellService shellService;

    ShellControllerTests() {
    }

    @BeforeAll
    void setUp() {
        log.info("*** STARTING DEVTOOLS CONTROLLER TESTS ***");
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING DEVTOOLS CONTROLLER TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Launch shell endpoint")
    void launchShellTest() throws Exception {

        when(shellService.launchShell(dataController, patternController, null)).thenReturn(true);

        mockMvc.perform(get("/devtools/launch-shell/{password}", "dummyPassword"))
                .andExpect(status().isUnauthorized());
    }
}
