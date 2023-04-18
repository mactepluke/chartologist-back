package com.syngleton.chartomancy.controller;

import com.syngleton.chartomancy.model.User;
import com.syngleton.chartomancy.model.Pattern;
import com.syngleton.chartomancy.dto.PatternSettingsDTO;
import com.syngleton.chartomancy.service.PatternService;
import com.syngleton.chartomancy.factory.PatternSettings;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@Log4j2
@RestController
@RequestMapping("/pattern")
@Scope("request")
public class PatternController {

    private final PatternService patternService;

    @Autowired
    public PatternController(PatternService patternService) {
        this.patternService = patternService;
    }

    //TODO implement user scope pattern creation
    //http://localhost:8080/pattern/create
    @GetMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Pattern>> create(@RequestBody PatternSettingsDTO settingsInputDTO, User user) {

        HttpStatus status = NO_CONTENT;
        List<Pattern> patterns = null;

        if (user != null && user.getUserSessionData() != null && user.getUserSessionData().getGraph() != null) {
            patterns = patternService.create(new PatternSettings.Builder().map(settingsInputDTO).graph(user.getUserSessionData().getGraph()));
        }

        if (patterns != null) {
            log.info("Successfully created patterns.");
            status = OK;
        } else {
            log.warn("Could not create patterns.");
        }

        return new ResponseEntity<>(patterns, status);
    }

    //http://localhost:8080/pattern/print-patterns
    @GetMapping("/print-patterns")
    public ResponseEntity<Boolean> printPatterns(User user) {

        HttpStatus status = NO_CONTENT;
        boolean result = false;

        if (user != null && user.getUserSessionData() != null && user.getUserSessionData().getPatterns() != null) {
            if (patternService.printPatterns(user.getUserSessionData().getPatterns())) {
                status = OK;
                result = true;
            } else {
                log.warn("Could not print patterns.");
            }
        }
        return new ResponseEntity<>(result, status);
    }

    //http://localhost:8080/pattern/print-patterns
    @GetMapping("/print-patterns-list")
    public ResponseEntity<Boolean> printPatternsList(User user) {

        HttpStatus status = NO_CONTENT;
        boolean result = false;


        if (user != null && user.getUserSessionData() != null && user.getUserSessionData().getPatterns() != null) {
            if (patternService.printPatternsList(user.getUserSessionData().getPatterns())) {
                status = OK;
                result = true;
            } else {
                log.warn("Could not print patterns.");
            }
        }
        return new ResponseEntity<>(result, status);
    }

    //http://localhost:8080/pattern/compute
    @GetMapping("/compute")
    public ResponseEntity<List<Pattern>> compute(User user) {

        HttpStatus status = NO_CONTENT;
        List<Pattern> patterns = null;

        if (user != null
                && user.getUserSessionData() != null
                && user.getUserSessionData().getPatterns() != null
                && user.getUserSessionData().getGraph() != null) {

            patterns = patternService.compute(user.getUserSessionData().getPatterns(), user.getUserSessionData().getGraph());
            if (patterns != null) {
                status = OK;
            } else {
                log.warn("Could not compute patterns.");
            }

        }
        return new ResponseEntity<>(patterns, status);
    }


}
