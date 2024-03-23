package co.syngleton.chartomancer.user_management;

import co.syngleton.chartomancer.user_controller.TestUser;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class UserServiceTests {

    @Autowired
    private UserService userService;
    private final UserRepository inMemoryUserRepository = new InMemoryUserRepository();
    @MockBean(name = "userRepository")
    private UserRepository userRepository;

    @BeforeAll
    void setUp() {
        log.info("*** STARTING USER SERVICE TESTS ***");

        when(userRepository.create(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return inMemoryUserRepository.create(user);
        });

        when(userRepository.read(anyString())).thenAnswer(invocation -> {
            String username = invocation.getArgument(0);
            return inMemoryUserRepository.read(username);
        });

        when(userRepository.update(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return inMemoryUserRepository.update(user);
        });

        doAnswer(invocation -> {
            String username = invocation.getArgument(0);
            inMemoryUserRepository.delete(username);
            return null;
        }).when(userRepository).delete(anyString());
    }

    @AfterAll
    void tearDown() {
        log.info("*** ENDING USER SERVICE TESTS ***");
    }

    @Test
    @DisplayName("[UNIT] Successfully performs basic CRUD operations on user repository")
    void basicCRUDTest() {

        User user = new TestUser();

        assertNull(userService.find(user.getUsername()));
        assertEquals(user, userService.create(user.getUsername(), user.getPassword()));
        assertEquals(user, userService.find(user.getUsername()));

        user.setPassword("newPassword");

        assertEquals(user, userService.update(user.getUsername(), user));
        assertEquals(user, userService.find(user.getUsername()));

        userService.delete(user.getUsername());

        assertNull(userService.find(user.getUsername()));
    }



}
