package co.syngleton.chartomancer.core_entities;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class PatternTests {

    @BeforeAll
    void setUp() {
        log.info("*** STARTING PATTERN TESTS ***");
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING PATTERN TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Creates PatternBox from Pattern list by scope")
    void createPatternBoxFromPatternListByScope() {

        int scopes = 10;
        BasicPattern basicPattern = new BasicPattern(new ArrayList<>(), 30, 15, Symbol.UNDEFINED, Timeframe.UNKNOWN, null);
        List<Pattern> patterns = new ArrayList<>();

        for (int i = 1; i <= scopes; i++) {
            patterns.add(new PredictivePattern(basicPattern, i));
        }

        PatternBox patternBox = new PatternBox(patterns);
        log.debug("Patterns: {}", patternBox.getPatterns());

        assertEquals(scopes, patternBox.getPatterns().size());
    }


}
