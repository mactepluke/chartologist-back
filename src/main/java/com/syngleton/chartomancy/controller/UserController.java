package com.syngleton.chartomancy.controller;

import com.syngleton.chartomancy.model.User;
import com.syngleton.chartomancy.service.UserService;
import com.syngleton.chartomancy.util.Format;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@Log4j2
@RestController
@RequestMapping("/user")
@Scope("request")
public class UserController extends BasicController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //http://localhost:8080/user/create
    @PostMapping("/create")
    public ResponseEntity<User> create(@RequestBody User requestedUser) {

        HttpStatus status;
        User pmbUser = null;
        String email = requestedUser.getEmail().toLowerCase();
        String password = requestedUser.getPassword();
        requestedUser.setFirstName(Format.trimToMax(requestedUser.getFirstName(), USER_NAME));
        requestedUser.setLastName(Format.trimToMax(requestedUser.getLastName(), USER_NAME));

        acknowledgeRequest("Create user", email);

        if (emailIsValid(email) && passwordIsValid(password)) {

            pmbUser = userService.create(email, password);

            if (pmbUser == null) {
                log.error("User already exists with email: {}", email);
                status = OK;
            } else {
                log.info("User created with id: {}", (pmbUser.getUserId() == null ? "<no_id>" : pmbUser.getUserId()));
                status = CREATED;
            }
        } else {
            status = BAD_REQUEST;
        }
        return new ResponseEntity<>(pmbUser, status);
    }

    //http://localhost:8080/user/login?email=<email>&password=<password>
    @GetMapping("/login")
    public ResponseEntity<User> login(@RequestParam String email, @RequestParam String password) {

        HttpStatus status;
        User user = null;

        email = email.toLowerCase();
        acknowledgeRequest("Login", email);

        if (emailIsValid(email) && passwordIsValid(password)) {

            user = userService.getByEmailAndEnabled(email);

            if (user == null) {
                log.error("No user found with email: {}", email);
                status = NO_CONTENT;
            } else {
                if (user.getPassword().equals(password)) {
                    log.info("Login request successful.");
                    status = OK;
                } else {
                    log.error("Invalid password.");
                    status = UNAUTHORIZED;
                    user = null;
                }
            }
        } else {
            status = BAD_REQUEST;
        }

        return new ResponseEntity<>(user, status);
    }

    //http://localhost:8080/user/find/<email>
    @GetMapping("/find/{email}")
    public ResponseEntity<User> find(@PathVariable String email) {

        HttpStatus status;
        User user = null;
        email = email.toLowerCase();

        if (emailIsValid(email)) {
            acknowledgeRequest("Find user", email);

            user = userService.getByEmail(email);

            if (user == null) {
                log.error("No user found with email: {}", email);
                status = NO_CONTENT;
            } else {
                log.info("Find request successful.");
                status = OK;
            }
        } else {
            status = BAD_REQUEST;
        }
        return new ResponseEntity<>(user, status);
    }

    //http://localhost:8080/pmbuser/update?email=<email>
    @PutMapping("/update")
    public ResponseEntity<User> update(@RequestParam String email, @RequestBody User editedUser) {

        HttpStatus status;
        User user = null;
        email = email.toLowerCase();

        acknowledgeRequest("Update user", email);

        if (emailIsValid(email) && passwordIsValid(editedUser.getPassword())) {

            editedUser.setFirstName(Format.trimToMax(editedUser.getFirstName(), USER_NAME));
            editedUser.setLastName(Format.trimToMax(editedUser.getLastName(), USER_NAME));

            user = userService.update(email, editedUser);

            if (user != null) {
                status = OK;
                log.info("Update request successful.");
            } else {
                status = INTERNAL_SERVER_ERROR;
                log.error("Couldn't update user.");
            }
        }
        else    {
            status = BAD_REQUEST;
        }
        return new ResponseEntity<>(user, status);
    }

}