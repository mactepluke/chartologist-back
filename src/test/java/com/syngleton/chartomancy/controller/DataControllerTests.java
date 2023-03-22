package com.syngleton.chartomancy.controller;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
class DataControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Load data endpoint")
    void load() throws Exception {
        mockMvc.perform(get("/data/load?path={id}", "FILE_PATH"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Launch analyse endpoint")
    void analyse() throws Exception {
        mockMvc.perform(get("/data/analyse"))
                .andExpect(status().isOk());
    }
}
