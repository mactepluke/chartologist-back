package com.syngleton.chartomancy.controller;

import com.syngleton.chartomancy.model.Graph;
import com.syngleton.chartomancy.service.DataService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
class DataControllerTests {

    @Value("${test_data_file_path}")
    private String testDataFilePath;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DataService dataService;
    private Graph graph;


    @BeforeAll
    void setUp() {
        log.info("*** STARTING DATA CONTROLLER TESTS ***");
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING DATA CONTROLLER TESTS ***");
    }


    @Test
    @DisplayName("Load data endpoint")
    void load() throws Exception {

        when(dataService.load(testDataFilePath)).thenReturn(graph);

        mockMvc.perform(get("/data/load?path={id}", testDataFilePath))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Launch print graph endpoint")
    void printGraph() throws Exception {

        when(dataService.printGraph(graph)).thenReturn(true);

        mockMvc.perform(get("/data/print-graph"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Launch analyse endpoint")
    void analyse() throws Exception {
        mockMvc.perform(get("/data/analyse"))
                .andExpect(status().isOk());
    }

}
