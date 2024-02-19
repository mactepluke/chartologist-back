package co.syngleton.chartomancer.signaling;

import co.syngleton.chartomancer.configuration.MockConfig;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Set;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = MockConfig.class)
@ActiveProfiles("test")
class EmailServiceTests {

    private final Set<String> addresses = Set.of("luc.metz@icloud.com");
    @Autowired
    private EmailingSignalingService emailingSignalingService;

/*    @Test
    @Disabled
    @DisplayName("[UNIT] Sends basic email to admin email")
    void sendBasicEmailTest() {
        emailingSignalingService.sendSignal("Test email chartomancer", "Test is successful!");
    }*/
}
