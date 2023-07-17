package co.syngleton.chartomancer.controller;

import co.syngleton.chartomancer.configuration.DataConfigTest;
import co.syngleton.chartomancer.service.domain.PatternService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
    @DisplayName("[UNIT] Create pattern endpoint")
    void createTest() throws Exception {

        mockMvc.perform(get("/pattern/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"autoconfig\":\"TEST\",\"patternType\":\"BASIC\",\"name\":\"Test name\"}")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("[UNIT] Print patterns endpoint")
    void printAppDataPatternsTest() throws Exception {

        when(patternService.printPatterns(null)).thenReturn(true);

        mockMvc.perform(get("/pattern/print-patterns"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("[UNIT] Print patterns list endpoint")
    void printAppDataPatternsListTest() throws Exception {

        when(patternService.printPatternsList(null)).thenReturn(true);

        mockMvc.perform(get("/pattern/print-patterns-list"))
                .andExpect(status().isNoContent());
    }

    //http://localhost:8080/pattern/compute
    @Test
    @DisplayName("[UNIT] Compute endpoint")
    void computeTest() throws Exception {

        mockMvc.perform(get("/pattern/compute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"autoconfig\":\"TEST\",\"computationType\":\"BASIC_ITERATION\"}")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }
}
