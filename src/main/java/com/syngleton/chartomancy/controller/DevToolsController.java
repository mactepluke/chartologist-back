package com.syngleton.chartomancy.controller;

import com.syngleton.chartomancy.model.User;
import com.syngleton.chartomancy.service.DevToolsService;
import com.syngleton.chartomancy.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Log4j2
@RestController
@RequestMapping("/devtools")
@Scope("request")
public class DevToolsController {

    private final DevToolsService devToolsService;
    private final DataController dataController;
    private final PatternController patternController;
    private final User devToolsUser;
    private final UserService userService;

    @Autowired
    public DevToolsController(DataController dataController,
                           PatternController patternController,
                              DevToolsService devToolsService,
                              User devToolsUser,
                              UserService userService) {
        this.dataController = dataController;
        this.patternController = patternController;
        this.devToolsService = devToolsService;
        this.devToolsUser = devToolsUser;
        this.userService = userService;
    }

    @GetMapping("/launch-shell/{password}")
    public ResponseEntity<Boolean> launchShell(@PathVariable String password) {

        HttpStatus status;
        boolean result = false;

        if (userService.matches(password, devToolsUser.getPassword())) {
            status = OK;
            result = devToolsService.launchShell(dataController, patternController, devToolsUser);
        } else {
            log.error("Invalid password for devToolsUser.");
            status = UNAUTHORIZED;
        }

        return new ResponseEntity<>(result, status);
    }

    @GetMapping("/run-script/{password}")
    public ResponseEntity<Boolean> runScript(@PathVariable String password) {

        HttpStatus status;
        boolean result = false;

        if (userService.matches(password, devToolsUser.getPassword())) {
            status = OK;
            result = devToolsService.runScript(dataController, patternController, devToolsUser);
        } else {
            log.error("Invalid password for devToolsUser.");
            status = UNAUTHORIZED;
        }

        return new ResponseEntity<>(result, status);
    }

}
