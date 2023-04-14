package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.model.dataloading.Candle;
import com.syngleton.chartomancy.model.dataloading.Graph;
import com.syngleton.chartomancy.model.dataloading.Timeframe;
import com.syngleton.chartomancy.model.patterns.Pattern;
import com.syngleton.chartomancy.model.patterns.PatternType;
import com.syngleton.chartomancy.service.dataloading.DataService;
import com.syngleton.chartomancy.service.patterns.PatternService;
import com.syngleton.chartomancy.service.patterns.PatternSettings;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatternServicesTests {

    private final static int TEST_GRAPH_LENGTH = 100;
    private final static int TEST_GRAPH_STARTING_DATETIME = 1666051200;
    private final static Timeframe TEST_TIMEFRAME = Timeframe.DAY;
    private final static int TEST_STARTING_OPEN = 1500;
    private final static float VARIABILITY_COEF = 300;

    private Graph mockGraph;

    @Autowired
    PatternService patternService;
    @MockBean
    DataService dataService;
    /*@MockBean
    PatternFactory patternFactory;*/

    @BeforeAll
    void setUp() {
        log.info("*** STARTING PATTERN SERVICE TESTS ***");
        List<Candle> mockCandles = new ArrayList<>();

        for (int i = 0; i < TEST_GRAPH_LENGTH; i++) {
            Random rd = new Random();
            float span = (float) Math.random() * VARIABILITY_COEF;
            boolean direction = rd.nextBoolean();
            float open = (i == 0) ? TEST_STARTING_OPEN : mockCandles.get(i - 1).close();
            float close = (direction) ? open + span : open - span;
            float high = (float) ((direction) ? close + Math.random() * VARIABILITY_COEF : open + Math.random() * VARIABILITY_COEF);
            float low = (float) ((direction) ? open - Math.random() * VARIABILITY_COEF : close - Math.random() * VARIABILITY_COEF);
            float volume = (float) Math.random() * VARIABILITY_COEF * TEST_STARTING_OPEN;

            mockCandles.add(new Candle(
                    LocalDateTime.ofEpochSecond(TEST_GRAPH_STARTING_DATETIME + TEST_TIMEFRAME.durationInSeconds * i, 0, ZoneOffset.UTC),
                    open, high, Math.min(low, 0), Math.min(close, 0), volume
            ));
        }

        mockGraph = new Graph("Mock graph", "MOCKSYMBOL", TEST_TIMEFRAME, mockCandles);
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING PATTERN SERVICE TESTS ***");
    }

    @Test
    @DisplayName("Create patterns from mock graph")
    void create() {

        List<Pattern> patterns;

        PatternSettings.Builder testSettings = new PatternSettings.Builder()
                .autoconfig(PatternSettings.Autoconfig.TEST)
                .graph(mockGraph)
                .patternType(PatternType.BASIC);
        patterns = patternService.create(testSettings);

        assertFalse(patterns.isEmpty());
        assertEquals( TEST_GRAPH_LENGTH / testSettings.build().getLength(), patterns.size());
    }
}
