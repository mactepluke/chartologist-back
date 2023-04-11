package com.syngleton.chartomancy.controller;

import com.syngleton.chartomancy.service.devtools.DevToolsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@Log4j2
@RestController
@RequestMapping("/devtools")
@Scope("request")
public class DevToolsController {

    private final DevToolsService devToolsService;
    private final DataController dataController;
    private final PatternController patternController;

    @Autowired
    public DevToolsController(DataController dataController,
                           PatternController patternController, DevToolsService devToolsService) {
        this.dataController = dataController;
        this.patternController = patternController;
        this.devToolsService = devToolsService;
    }

    //http://localhost:8080/devtools/launch-shell
    @GetMapping("/launch-shell")
    public ResponseEntity<Boolean> launchShell() {

        Boolean result = devToolsService.launchShell(dataController, patternController);

        return new ResponseEntity<>(result, OK);
    }

    //http://localhost:8080/devtools/run-script
    @GetMapping("/run-script")
    public ResponseEntity<Boolean> runScript() {

        Boolean result = devToolsService.runScript(dataController, patternController);

        return new ResponseEntity<>(result, OK);
    }

}
