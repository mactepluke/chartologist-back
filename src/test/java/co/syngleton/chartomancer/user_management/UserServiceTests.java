package co.syngleton.chartomancer.user_management;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class UserServiceTests {

    private  UserService userService;
    private  UserFactory userFactory;
    InMemoryUserRepository userRepository = new InMemoryUserRepository();
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String VALID_PASSWORD = "123AZEaze#";

    @BeforeAll
    void setUp() {
        log.info("*** STARTING USER SERVICE TESTS ***");

        userFactory = new InMemoryUserFactory();
        userService = new DefaultUserService(userRepository, passwordEncoder, userFactory);
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING USER SERVICE TESTS ***");
        User testUser = userFactory.create("testUsername", "testPassword", "************");
        userService.delete(testUser.getUsername());
    }


    @Test
    @DisplayName("[UNIT] Successfully performs basic CRUD operations on user repository")
    void basicCRUDTest() {

        User user = userFactory.create("testUsername", "testPassword", "************");

        assertNull(userService.find(user.getUsername()));
        assertEquals(user, userService.create(user.getUsername(), user.getPassword()));
        assertEquals(user, userService.find(user.getUsername()));

        user.setPassword(VALID_PASSWORD);

        assertEquals(user, userService.update(user.getUsername(), user));
        assertEquals(user, userService.find(user.getUsername()));

        userService.delete(user.getUsername());

        assertNull(userService.find(user.getUsername()));
    }



}
