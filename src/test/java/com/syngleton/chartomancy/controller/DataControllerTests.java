package com.syngleton.chartomancy.controller;

import com.syngleton.chartomancy.configuration.DataConfigTest;
import com.syngleton.chartomancy.service.DataService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
class DataControllerTests {

    @Value("${test_data_folder_name}")
    private String testDataFolderName;
    private String testDataFilePath;
    @Value("#{'${test_data_files_names}'.split(',')}")
    private List<String> testDataFilesNames;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DataService dataService;


    @BeforeAll
    void setUp() {
        log.info("*** STARTING DATA CONTROLLER TESTS ***");
        testDataFilePath = "src/test/resources/" + testDataFolderName + testDataFilesNames.get(0);
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING DATA CONTROLLER TESTS ***");
    }


    @Test
    @DisplayName("[UNIT] Load data endpoint")
    void loadTest() throws Exception {

        when(dataService.loadGraph(testDataFilePath)).thenReturn(null);

        mockMvc.perform(get("/data/load?path={id}", testDataFilePath))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("[UNIT] Print app data graphs endpoint")
    void printAppDataGraphsTest() throws Exception {

        when(dataService.printGraph(null)).thenReturn(true);

        mockMvc.perform(get("/data/print-graph"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("[UNIT] Launch analyse endpoint")
    void analyseTest() throws Exception {
        mockMvc.perform(get("/data/analyse"))
                .andExpect(status().isOk());
    }

}
