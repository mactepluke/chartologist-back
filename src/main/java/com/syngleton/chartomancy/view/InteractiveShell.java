package com.syngleton.chartomancy.view;

import com.syngleton.chartomancy.controller.DataController;
import com.syngleton.chartomancy.controller.PatternController;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class InteractiveShell implements Runnable {

    private final DataController dataController;
    private final PatternController patternController;

    public InteractiveShell(DataController dataController,
                              PatternController patternController) {
        this.dataController = dataController;
        this.patternController = patternController;
    }

    @Override
    public void run() {
        log.info("*** SHELL LAUNCHED ***");
        log.info(dataController.load("/path"));
        log.info(dataController.analyse());
    }
}
