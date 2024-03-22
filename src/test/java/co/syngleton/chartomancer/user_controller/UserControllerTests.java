package co.syngleton.chartomancer.user_controller;

import co.syngleton.chartomancer.security.AuthRequest;
import co.syngleton.chartomancer.user_management.User;
import co.syngleton.chartomancer.user_management.UserService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    private User mockUser;
    private static final String VALID_PASSWORD = "123AZEaze#";

    @BeforeAll
    void setUp() {
        log.info("*** STARTING BACKTESTING ENDPOINTS TESTS ***");
        this.mockUser = new TestUser();
    }

    @AfterAll
    void tearDown() {
        log.info("*** MICROSERVICE BACKTESTING ENDPOINTS TESTS FINISHED ***");
    }

    @Test
    @DisplayName("[UNIT] Endpoint '/user/get' is accessible")
    void getUserTest() throws Exception {

        when(userService.find(this.mockUser.getUsername())).thenReturn(this.mockUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/get")
                .param("username", this.mockUser.getUsername()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[UNIT] User not found in '/user/get' endpoint")
    void getUserNotFoundTest() throws Exception {

        when(userService.find(this.mockUser.getUsername())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/get")
                        .param("username", this.mockUser.getUsername()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("[UNIT] Endpoint '/user/create' is accessible")
    void createUserTest() throws Exception {

        AuthRequest authRequest = new AuthRequest(this.mockUser.getUsername(), VALID_PASSWORD);

        when(userService.create(authRequest.getUsername(), authRequest.getPassword())).thenReturn(this.mockUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":" + "\"" + authRequest.getUsername() +
                                "\"" + ",\"password\":" + "\"" + authRequest.getPassword() + "\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("[UNIT] Endpoint '/user/create' is accessible")
    void createUserWithInvalidPasswordTest() throws Exception {

        AuthRequest authRequest = new AuthRequest(this.mockUser.getUsername(), this.mockUser.getPassword());

        when(userService.create(authRequest.getUsername(), authRequest.getPassword())).thenReturn(this.mockUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":" + "\"" + authRequest.getUsername() +
                                "\"" + ",\"password\":" + "\"" + authRequest.getPassword() + "\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[UNIT] Endpoint '/user/create' is accessible")
    void createUserWithInvalidNameTest() throws Exception {

        AuthRequest authRequest = new AuthRequest("   ", VALID_PASSWORD);

        when(userService.create(authRequest.getUsername(), authRequest.getPassword())).thenReturn(this.mockUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":" + "\"" + authRequest.getUsername() +
                                "\"" + ",\"password\":" + "\"" + authRequest.getPassword() + "\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }



}
