package co.syngleton.chartomancer.core_entities;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.configuration.GlobalTestConfig;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = GlobalTestConfig.class)
@ActiveProfiles("test")
class PatternBoxTests {

    @BeforeAll
    void setUp() {
        log.info("*** STARTING PATTERNBOX TESTS ***");
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING PATTERNBOX TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Creates PatternBox from Pattern list by scope")
    void createPatternBoxFromPatternListByScope() {

        int scopes = 10;

        BasicPattern basicPattern = new BasicPattern(Collections.emptyList(), 30, Symbol.UNDEFINED, Timeframe.UNKNOWN);
        List<Pattern> patterns = new ArrayList<>();

        for (int i = 1; i <= scopes; i++) {
            patterns.add(new ComputablePattern(basicPattern, i));
        }

        PatternBox patternBox = new PatternBox(patterns);
        log.debug("Patterns: {}", patternBox.getPatterns());

        assertEquals(scopes, patternBox.getPatterns().size());
    }


}
