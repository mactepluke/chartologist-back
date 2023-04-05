package com.syngleton.chartomancy.controller;

import com.syngleton.chartomancy.service.patterns.PatternService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    //http://localhost:8080/pattern/create
    @GetMapping("/create")
    public ResponseEntity<Boolean> create() {

        HttpStatus status;

        if (patternService.create())    {
            log.info("Successfully created patterns.");
            status = OK;
        } else {
            log.warn("Could not create patterns.");
            status = NO_CONTENT;
        }

        return new ResponseEntity<>(true, status);
    }

    //http://localhost:8080/pattern/print-patterns
    @GetMapping("/print-patterns")
    public HttpStatus printPatterns() {

        HttpStatus status = OK;

        patternService.printPatterns();

        return status;
    }

    //http://localhost:8080/pattern/print-patterns
    @GetMapping("/print-patterns-list")
    public HttpStatus printPatternsList() {

        HttpStatus status = OK;

        patternService.printPatternsList();

        return status;
    }
}
