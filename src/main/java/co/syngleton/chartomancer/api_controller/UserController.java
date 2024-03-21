package co.syngleton.chartomancer.api_controller;

import co.syngleton.chartomancer.security.AuthResponse;
import co.syngleton.chartomancer.user_management.User;
import co.syngleton.chartomancer.user_management.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/user")
@Validated
@Scope("request")
@AllArgsConstructor
class UserController {
    private final UserService userService;

    @PostMapping("/create")
    ResponseEntity<User> createUser() {
        return null;
    }

    @PostMapping("/login")
    ResponseEntity<AuthResponse> loginUser() {
        return null;
    }

    @PutMapping("/update")
    ResponseEntity<User> updateUser() {
        return null;
    }

    @DeleteMapping("/delete")
    ResponseEntity<User> deleteUser() {
        return null;
    }

}
