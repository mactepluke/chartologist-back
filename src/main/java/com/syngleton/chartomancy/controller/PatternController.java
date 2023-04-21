package com.syngleton.chartomancy.controller;

import com.syngleton.chartomancy.analytics.ComputationSettings;
import com.syngleton.chartomancy.data.AppData;
import com.syngleton.chartomancy.dto.ComputationSettingsDTO;
import com.syngleton.chartomancy.model.charting.Pattern;
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
    private final AppData appData;

    @Autowired
    public PatternController(PatternService patternService,
                             AppData appData) {
        this.patternService = patternService;
        this.appData = appData;
    }

    //TODO implement multiple patterns/graph creation
    //http://localhost:8080/pattern/create
    @GetMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Pattern>> create(@RequestBody PatternSettingsDTO settingsInputDTO) {

        HttpStatus status = NO_CONTENT;
        List<Pattern> patterns = null;

        /*if (settingsInputDTO != null
                && appData.getGraphs() != null
                && !appData.getGraphs().isEmpty()) {
            patterns = patternService.createPatterns(new PatternSettings.Builder()
                    .map(settingsInputDTO)
                    .graph(appData.getGraphs().get(0)));
        }*/

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
    public ResponseEntity<Boolean> printAppDataPatterns() {

        HttpStatus status = NO_CONTENT;
        boolean result = false;

/*        if ((appData.getPatternsList() != null) && (!appData.getPatternsList().isEmpty())) {

            for (List<Pattern> patterns : appData.getPatternsList()) {
                patternService.printPatterns(patterns);
            }
            status = OK;
            result = true;
        } else {
            log.warn("Could not print patterns.");
        }*/
        return new ResponseEntity<>(result, status);
    }

    //http://localhost:8080/pattern/print-patterns
    @GetMapping("/print-patterns-list")
    public ResponseEntity<Boolean> printAppDataPatternsList() {

        HttpStatus status = NO_CONTENT;
        boolean result = false;
/*
        if ((appData.getPatternsList() != null) && (!appData.getPatternsList().isEmpty())) {

            for (List<Pattern> patterns : appData.getPatternsList()) {
                patternService.printPatternsList(patterns);
            }
            status = OK;
            result = true;
        } else {
            log.warn("Could not print patterns list.");
        }*/
        return new ResponseEntity<>(result, status);
    }

    //TODO aggregate computation by matching ChartObject types
    //http://localhost:8080/pattern/compute
    @GetMapping("/compute")
    public ResponseEntity<List<Pattern>> compute(@RequestBody ComputationSettingsDTO settingsInputDTO) {

        HttpStatus status = NO_CONTENT;
        List<Pattern> patterns = null;

        if (settingsInputDTO != null
                && appData.getPatternBoxes() != null
                && !appData.getPatternBoxes().isEmpty()
                && appData.getGraphs() != null
                && !appData.getGraphs().isEmpty()) {

            /*patterns = patternService.computePatterns(new ComputationSettings.Builder()
                    .map(settingsInputDTO)
                    .graph(appData.getGraphs().get(0))
                    .patterns(appData.getPatternsList().get(0)));*/
            if (patterns != null) {
                status = OK;
            } else {
                log.warn("Could not compute patterns.");
            }

        }
        return new ResponseEntity<>(patterns, status);
    }


}
