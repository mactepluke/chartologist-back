package com.syngleton.chartomancy.controller;

import com.syngleton.chartomancy.service.data.DataService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;


@Log4j2
@RestController
@RequestMapping("/data")
@Scope("request")
public class DataController {

    private final DataService dataService;

    @Autowired
    public DataController(DataService dataService) {
        this.dataService = dataService;
    }


    //http://localhost:8080/data/load?path=<path>
    @GetMapping("/load")
    public ResponseEntity<Boolean> load(@RequestParam String path) {

        HttpStatus status = OK;

        dataService.load(path);

        return new ResponseEntity<>(true, status);
    }

    //http://localhost:8080/data/print-graph
    @GetMapping("/print-graph")
    public HttpStatus printGraph() {

        HttpStatus status = OK;

        dataService.printGraph();

        return status;
    }

    //http://localhost:8080/data/analyse
    @GetMapping("/analyse")
    public ResponseEntity<Boolean> analyse() {

        HttpStatus status = OK;

        log.info("Analyzing data.");

        return new ResponseEntity<>(true, status);
    }
}
