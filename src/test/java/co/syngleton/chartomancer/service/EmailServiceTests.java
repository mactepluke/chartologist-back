package co.syngleton.chartomancer.service;

import co.syngleton.chartomancer.configuration.DataConfigTest;
import co.syngleton.chartomancer.signaling.service.EmailService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataConfigTest.class)
@ActiveProfiles("test")
class EmailServiceTests {

    @Autowired
    private EmailService emailService;

    @Test
    @Disabled
    @DisplayName("[UNIT] Sends basic email to admin email")
    void sendBasicEmailTest() {
        emailService.sendEmail("luc.metz@icloud.com", "Test email chartomancer", "Test is successful!");
    }
}
