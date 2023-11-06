package co.syngleton.chartomancer.factory;

import co.syngleton.chartomancer.configuration.DataConfigTest;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
@ActiveProfiles("test")
class GraphFactoryTests {


    @BeforeAll
    void setUp() {
        log.info("*** STARTING GRAPH FACTORY TESTS ***");
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING GRAPH FACTORY TESTS ***");
    }

    @Test
    @Disabled
    @DisplayName("[UNIT]")
    void graphFactoryTest() {
        //TODO: implement test for all factories
    }


}
