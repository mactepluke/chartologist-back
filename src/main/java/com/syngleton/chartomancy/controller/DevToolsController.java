package com.syngleton.chartomancy.controller;

import com.syngleton.chartomancy.data.AppData;
import com.syngleton.chartomancy.service.DevToolsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Value("${devtools_password}")
    private String devToolsPassword;

    private final DevToolsService devToolsService;
    private final DataController dataController;
    private final PatternController patternController;
    private final AppData appData;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DevToolsController(DataController dataController,
                           PatternController patternController,
                              DevToolsService devToolsService,
                              AppData appData,
                              PasswordEncoder passwordEncoder) {
        this.dataController = dataController;
        this.patternController = patternController;
        this.devToolsService = devToolsService;
        this.appData = appData;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/launch-shell/{password}")
    public ResponseEntity<Boolean> launchShell(@PathVariable String password) {

        HttpStatus status;
        boolean result = false;

        if (passwordEncoder.matches(password, devToolsPassword)) {
            status = OK;
            result = devToolsService.launchShell(dataController, patternController, appData);
        } else {
            log.error("Invalid password for devToolsUser.");
            status = UNAUTHORIZED;
        }

        return new ResponseEntity<>(result, status);
    }
}
