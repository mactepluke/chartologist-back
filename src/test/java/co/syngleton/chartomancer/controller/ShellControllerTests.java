package co.syngleton.chartomancer.controller;

import co.syngleton.chartomancer.configuration.DataConfigTest;
import co.syngleton.chartomancer.controller.devtools.PatternController;
import co.syngleton.chartomancer.data.CoreData;
import co.syngleton.chartomancer.service.misc.LaunchService;
import co.syngleton.chartomancer.controller.devtools.DataController;
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
    private LaunchService launchService;

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

        when(launchService.launchShell(dataController, patternController, null)).thenReturn(true);

        mockMvc.perform(get("/devtools/launch-shell/{password}", "dummyPassword"))
                .andExpect(status().isUnauthorized());
    }
}
