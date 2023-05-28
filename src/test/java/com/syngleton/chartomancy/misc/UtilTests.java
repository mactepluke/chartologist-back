package com.syngleton.chartomancy.misc;

import com.syngleton.chartomancy.configuration.DataConfigTest;
import com.syngleton.chartomancy.util.Calc;
import com.syngleton.chartomancy.util.Check;
import com.syngleton.chartomancy.util.Format;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
class UtilTests {

    private boolean result;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING UTIL TESTS ***");
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING UTIL TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Returns the positive percentage from an int part and total")
    void positivePercentageTest() {

        assertEquals(22, Calc.positivePercentage(77, 350));
        assertEquals(0, Calc.positivePercentage(-50, 350));
        assertEquals(14, Calc.positivePercentage(50, 350));
    }

    @Test
    @DisplayName("[UNIT] Returns the relative percentage from an int part and total")
    void relativePercentageTest() {

        assertEquals(22, Calc.relativePercentage(77, 350));
        assertEquals(-14, Calc.relativePercentage(-50, 350));
        assertEquals(100, Calc.relativePercentage(600, 350));
    }

    @Test
    @DisplayName("[UNIT] Returns the variation percentage from a start value and an end value")
    void variationPercentageTest() {
        assertEquals(25, Calc.variationPercentage(20, 25));
        assertEquals(-20, Calc.variationPercentage(25, 20));
    }

    @Test
    @DisplayName("[UNIT] Returns the float value rounded to two digits")
    void roundTwoDigitsTest() {
        assertEquals(0.35f, Format.roundTwoDigits(0.346f));
    }

    @Test
    @DisplayName("[UNIT] Returns the float value rounded to a relevant number of digits")
    void roundAccordinglyTest() {
        assertEquals(20349, Format.roundAccordingly(20349.34698f));
        assertEquals(129.4f, Format.roundAccordingly(129.44698f));
        assertEquals(30.45f, Format.roundAccordingly(30.44698f));
        assertEquals(1.458f, Format.roundAccordingly(1.45778f));
        assertEquals(0.4583f, Format.roundAccordingly(0.45832106f));
        assertEquals(0.04583f, Format.roundAccordingly(0.045832106f));
        assertEquals(0.004583f, Format.roundAccordingly(0.0045832106f));
        assertEquals(0.0004583f, Format.roundAccordingly(0.00045832106f));
        assertEquals(0.00005f, Format.roundAccordingly(0.000045832106f));
    }

}
