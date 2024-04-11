package co.syngleton.chartomancer.user_controller;

import co.syngleton.chartomancer.user_management.User;
import co.syngleton.chartomancer.user_management.UserDTO;
import co.syngleton.chartomancer.user_management.UserFactory;
import co.syngleton.chartomancer.user_management.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

import static co.syngleton.chartomancer.user_management.UserValidationConstants.PASSWORD_MESSAGE;
import static co.syngleton.chartomancer.user_management.UserValidationConstants.PASSWORD_PATTERN;

@Log4j2
@RestController
@RequestMapping("/user")
@Validated
@Scope("request")
@AllArgsConstructor
class UserController {
    private final UserService userService;
    private final UserFactory userFactory;

    @GetMapping("/get")
    ResponseEntity<UserDTO> get(@RequestParam final String username) {

        if (username == null || username.isEmpty()) throw new CannotFindUserException("Username cannot be empty.");

        final User user = userService.find(username);
        if (user == null) throw new CannotFindUserException("Cannot find user with username: " + username);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
                .body(userFactory.from(user));
    }

    @PostMapping("/create")
    ResponseEntity<UserDTO> create(@RequestBody @Valid final UserDTO userDTO) {

        if (!userDTO.password().matches(PASSWORD_PATTERN))  {
            throw new InvalidPasswordException(PASSWORD_MESSAGE);
        }

        final User user = userService.create(userDTO.username(), userDTO.password());
        if (user == null) throw new CannotHandleUserException("Cannot create user with username: " + userDTO.username());

        return new ResponseEntity<>(userFactory.from(user), HttpStatus.CREATED);
    }

    @GetMapping("/login")
    UserDTO login(Authentication authentication) {
        // The authentication object is populated after the end of the authentication process, output from the AuthenticationProvider.
        final User user = userService.find(authentication.getName());

        if (user == null) {
            return null;
        }
        return userFactory.from(user);
    }

    @PutMapping("/update")
    ResponseEntity<UserDTO> update(@RequestParam final String username, @RequestBody @Valid final UserDTO editedUser) {

        User userToUpdate = userFactory.from(editedUser);
        userToUpdate = userService.update(username, userToUpdate);

        if (userToUpdate == null) throw new CannotHandleUserException("Cannot update user with username: " + username);

        return new ResponseEntity<>(userFactory.from(userToUpdate), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    ResponseEntity<Void> delete(@RequestParam final String username) {

        userService.delete(username);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
