package com.syngleton.chartomancy.misc;

import com.syngleton.chartomancy.util.Format;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UtilTests {

    @BeforeAll
    void setUp() {
        log.info("*** STARTING UTIL TESTS ***");
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING UTIL TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Returns a byte percentage from an int part and total")
    void bytePositivePercentageTest() {

        assertEquals(22, Format.positivePercentage(77, 350));
        assertEquals(0, Format.positivePercentage(-50, 350));
        assertEquals(14, Format.positivePercentage(50, 350));
    }

    @Test
    @DisplayName("[UNIT] Returns a byte percentage from an int part and total")
    void byteRelativePercentageTest() {

        assertEquals(22, Format.relativePercentage(77, 350));
        assertEquals(-14, Format.relativePercentage(-50, 350));
        assertEquals(100, Format.relativePercentage(600, 350));
    }

}
