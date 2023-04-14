package com.syngleton.chartomancy.controller;

import com.syngleton.chartomancy.devtools.DevToolsService;
import com.syngleton.chartomancy.model.User;
import com.syngleton.chartomancy.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
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
    @Autowired
    private DataController dataController;
    @Autowired
    private PatternController patternController;
    @MockBean
    private DevToolsService devToolsService;
    @MockBean
    private User devToolsUser;
    @MockBean
    private UserService userService;

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

        when(devToolsService.launchShell(dataController, patternController, devToolsUser)).thenReturn(true);
        when(userService.matches(eq("testPassword"), any())).thenReturn(true);

        mockMvc.perform(get("/devtools/launch-shell/{password}", "testPassword"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Run script endpoint")
    void runScript() throws Exception {

        when(devToolsService.runScript(dataController, patternController, devToolsUser)).thenReturn(true);
        when(userService.matches(eq("testPassword"), any())).thenReturn(true);


        mockMvc.perform(get("/devtools/run-script/{password}", "testPassword"))
                .andExpect(status().isOk());
    }
}
