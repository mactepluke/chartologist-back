package co.syngleton.chartomancer.core_entities;


import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.configuration.GlobalTestConfig;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = GlobalTestConfig.class)
@ActiveProfiles("test")
class PatternTests {

    private Pattern pattern;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING PATTERN TESTS ***");

        this.pattern = new BasicPattern(
                Collections.emptyList(),
                100,
                Symbol.UNDEFINED,
                Timeframe.UNKNOWN
        );

    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING PATTERN TESTS ***");
    }


    @Test
    @DisplayName("[UNIT] MultiComputable patterns return the correct price variation prediction")
    void multiComputablePatternsReturnCorrectPriceVariationPredictionTest() {

        MultiComputablePattern multiComputablePattern = new MultiComputablePattern(
                this.pattern,
                5
        );

        multiComputablePattern.setPriceVariationPrediction(5f, 1);
        multiComputablePattern.setPriceVariationPrediction(-4.5f, 2);
        multiComputablePattern.setPriceVariationPrediction(0, 3);
        multiComputablePattern.setPriceVariationPrediction(-6.3f, 4);
        multiComputablePattern.setPriceVariationPrediction(0, 5);

        assertEquals(-6.3f, multiComputablePattern.getPriceVariationPrediction());
        assertThrows(IllegalArgumentException.class, () -> multiComputablePattern.setPriceVariationPrediction(0, 6));
    }
}
