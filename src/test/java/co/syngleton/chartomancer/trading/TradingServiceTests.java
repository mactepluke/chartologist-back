package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.configuration.GlobalTestConfig;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = GlobalTestConfig.class)
@ActiveProfiles("test")
public class TradingServiceTests {

    @Autowired
    TradingService tradingService;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING TRADING SERVICE TESTS ***");

    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING TRADING SERVICE TESTS ***");

    }


}
