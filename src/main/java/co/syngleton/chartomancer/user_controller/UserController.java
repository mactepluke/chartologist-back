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
    ResponseEntity<UserDTO> get(@RequestParam final String username) {

        if (username == null || username.isEmpty()) throw new CannotFindUserException("Username cannot be empty.");

        final User user = userService.find(username);
        if (user == null) throw new CannotFindUserException("Cannot find user with username: " + username);

        return new ResponseEntity<>(UserDTO.fromEntity(user), HttpStatus.OK);
    }

    @PostMapping("/create")
    ResponseEntity<UserDTO> create(@RequestBody @Valid final AuthRequest authRequest) {

        final User user = userService.create(authRequest.getUsername(), authRequest.getPassword());
        if (user == null) throw new CannotHandleUserException("Cannot create user with username: " + authRequest.getUsername());

        return new ResponseEntity<>(UserDTO.fromEntity(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest authRequest) {

        return null;
    }

    @PutMapping("/update")
    ResponseEntity<UserDTO> update(@RequestParam final String username, @RequestBody @Valid final UserDTO editedUser) {

        User userToUpdate = UserDTO.toEntity(editedUser);
        userToUpdate = userService.update(username, userToUpdate);

        if (userToUpdate == null) throw new CannotHandleUserException("Cannot update user with username: " + username);

        return new ResponseEntity<>(UserDTO.fromEntity(userToUpdate), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    ResponseEntity<Void> delete(@RequestParam final String username) {

        userService.delete(username);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
