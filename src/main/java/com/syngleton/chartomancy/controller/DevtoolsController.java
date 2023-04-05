package com.syngleton.chartomancy.controller;

import com.syngleton.chartomancy.view.InteractiveShell;
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
public class DevtoolsController {

    private final DataController dataController;
    private final PatternController patternController;

    @Autowired
    public DevtoolsController(DataController dataController,
                              PatternController patternController) {
        this.dataController = dataController;
        this.patternController = patternController;
    }

    //http://localhost:8080/devtools/launch-shell
    @GetMapping("/launch-shell")
    public ResponseEntity<Boolean> launchShell() {

        Thread interactiveShell = new Thread(new InteractiveShell(dataController, patternController));
        interactiveShell.start();

        return new ResponseEntity<>(true, OK);
    }

    //http://localhost:8080/devtools/run-script
    @GetMapping("/run-script")
    public ResponseEntity<Boolean> runScript() {

        Thread scriptRunner = new Thread(new ScriptRunner(dataController, patternController));
        scriptRunner.start();

        return new ResponseEntity<>(true, OK);
    }

}
