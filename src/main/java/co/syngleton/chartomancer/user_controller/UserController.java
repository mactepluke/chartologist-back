package co.syngleton.chartomancer.user_controller;

import co.syngleton.chartomancer.security.AuthRequest;
import co.syngleton.chartomancer.security.AuthResponse;
import co.syngleton.chartomancer.user_management.User;
import co.syngleton.chartomancer.user_management.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/get")
    ResponseEntity<UserDTO> get(@RequestParam String username) {

        if (username == null || username.isEmpty()) throw new CannotFindUserException("Username cannot be empty.");

        User user = userService.find(username);
        if (user == null) throw new CannotFindUserException("Cannot find user with username: " + username);

        return new ResponseEntity<>(UserDTO.fromEntity(user), HttpStatus.OK);
    }

    @PostMapping("/create")
    ResponseEntity<UserDTO> create(@RequestBody @Valid AuthRequest authRequest) {

        User user = userService.create(authRequest.getUsername(), authRequest.getPassword());
        if (user == null) throw new CannotHandleUserException("Cannot create user with username: " + authRequest.getUsername());

        return new ResponseEntity<>(UserDTO.fromEntity(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest authRequest) {



        return null;
    }

    @PutMapping("/update-user")
    ResponseEntity<UserDTO> updateUser(@RequestParam String username, @RequestBody @Valid UserDTO editedUser) {

        User user = userService.find(username);
        if (user == null) throw new CannotFindUserException("Cannot find user with username: " + username);

        //User updatedUser = userService.update(username)

        return null;
    }

    @PutMapping("/update-user-and-pwd")
    ResponseEntity<UserDTO> updateUserAndPassword(@RequestParam String username, @RequestBody @Valid UserDTO editedUser) {

        return null;
    }

    @DeleteMapping("/delete")
    ResponseEntity<Void> delete(@RequestParam String username) {
        return null;
    }

}
