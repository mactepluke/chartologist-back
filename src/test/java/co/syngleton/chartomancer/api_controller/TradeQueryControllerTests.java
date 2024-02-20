package co.syngleton.chartomancer.api_controller;

import co.syngleton.chartomancer.configuration.MockConfig;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = MockConfig.class)
@ActiveProfiles("test")
public class TradeQueryControllerTests {
}
