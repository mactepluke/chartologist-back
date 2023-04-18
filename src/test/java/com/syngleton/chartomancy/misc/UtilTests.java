package com.syngleton.chartomancy.misc;

import com.syngleton.chartomancy.util.Format;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

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

        assertEquals(22, Format.bytePositivePercentage(77, 350));
        assertEquals(0, Format.bytePositivePercentage(-50, 350));
        assertEquals(14, Format.bytePositivePercentage(50, 350));
    }

    @Test
    @DisplayName("[UNIT] Returns a byte percentage from an int part and total")
    void byteRelativePercentageTest() {

        assertEquals(22, Format.byteRelativePercentage(77, 350));
        assertEquals(-14, Format.byteRelativePercentage(-50, 350));
        assertEquals(100, Format.byteRelativePercentage(600, 350));
    }

}
