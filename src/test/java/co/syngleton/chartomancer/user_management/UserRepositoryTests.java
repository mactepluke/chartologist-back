package co.syngleton.chartomancer.user_management;

import co.syngleton.chartomancer.user_controller.TestUser;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING USER REPOSITORY TESTS ***");

    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING USER REPOSITORY TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Successfully performs basic CRUD operations on user repository")
    void basicCRUDTest() {

        User user = new TestUser();

        assertNull(userRepository.read(user.getUsername()));
        assertEquals(user, userRepository.create(user));
        assertEquals(user, userRepository.read(user.getUsername()));

        user.setPassword("newPassword");

        assertEquals(user, userRepository.update(user));
        assertEquals(user, userRepository.read(user.getUsername()));

        userRepository.delete(user.getUsername());

        assertNull(userRepository.read(user.getUsername()));
    }



}
