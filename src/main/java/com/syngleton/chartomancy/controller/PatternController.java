package com.syngleton.chartomancy.controller;

import com.syngleton.chartomancy.data.GenericData;
import com.syngleton.chartomancy.model.User;
import com.syngleton.chartomancy.model.dataloading.Graph;
import com.syngleton.chartomancy.model.patterns.Pattern;
import com.syngleton.chartomancy.model.patterns.PatternSettingsDTO;
import com.syngleton.chartomancy.service.patterns.PatternService;
import com.syngleton.chartomancy.service.patterns.PatternSettings;
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
    @GetMapping(path="/create/{user}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Pattern>> create(@RequestBody PatternSettingsDTO settingsInputDTO, @RequestBody User user) {

        HttpStatus status;
        List<Pattern> patterns;

        if (user == null)   {
            user = new User();
        }
        patterns = patternService.create(new PatternSettings.Builder().map(settingsInputDTO).graph(user.getGenericData().getGraph()));

        if (patterns != null)    {
            log.info("Successfully created patterns.");
            status = OK;
        } else {
            log.warn("Could not create patterns.");
            status = NO_CONTENT;
        }

        return new ResponseEntity<>(patterns, status);
    }

    //http://localhost:8080/pattern/print-patterns
    @GetMapping("/print-patterns")
    public HttpStatus printPatterns(List<Pattern> patterns) {

        HttpStatus status = OK;

        patternService.printPatterns(patterns);

        return status;
    }

    //http://localhost:8080/pattern/print-patterns
    @GetMapping("/print-patterns-list")
    public HttpStatus printPatternsList(List<Pattern> patterns) {

        HttpStatus status = OK;

        patternService.printPatternsList(patterns);

        return status;
    }
}
