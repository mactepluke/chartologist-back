package com.syngleton.chartomancy.controller;

import lombok.extern.log4j.Log4j2;
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

    //http://localhost:8080/data/load?path=<path>
    @GetMapping("/load")
    public ResponseEntity<Boolean> load(@RequestParam String path) {

        HttpStatus status = OK;

        return new ResponseEntity<>(true, status);
    }

    //http://localhost:8080/data/analyse
    @GetMapping("/analyse")
    public ResponseEntity<Boolean> analyse() {

        HttpStatus status = OK;

        return new ResponseEntity<>(true, status);
    }
}
