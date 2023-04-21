package com.syngleton.chartomancy.controller;

import com.syngleton.chartomancy.data.AppData;
import com.syngleton.chartomancy.model.charting.Graph;
import com.syngleton.chartomancy.service.DataService;
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
    private final AppData appData;

    @Autowired
    public DataController(DataService dataService,
                          AppData appData) {
        this.dataService = dataService;
        this.appData = appData;
    }


    //http://localhost:8080/data/load?path=<path>
    @GetMapping("/load")
    public ResponseEntity<Graph> load(@RequestParam String path) {

        HttpStatus status = NO_CONTENT;
        Graph graph;

        graph = dataService.loadGraph(path);

        if (graph != null)  {
            log.debug("Successfully loaded file: " + path);
            status = OK;
        }

        return new ResponseEntity<>(graph, status);
    }

    //http://localhost:8080/data/print-graph
    @GetMapping("/print-graph")
    public ResponseEntity<Boolean> printAppDataGraphs() {

        HttpStatus status = NO_CONTENT;
        boolean result = false;

        if ((appData.getGraphs() != null) && (!appData.getGraphs().isEmpty())) {
            for (Graph graph : appData.getGraphs()) {
                dataService.printGraph(graph);
            }
            status = OK;
        }

        return new ResponseEntity<>(result, status);
    }

    //http://localhost:8080/data/analyse
    @GetMapping("/analyse")
    public ResponseEntity<Boolean> analyse() {

        log.debug("Analyzing data.");

        return new ResponseEntity<>(true, OK);
    }
}
