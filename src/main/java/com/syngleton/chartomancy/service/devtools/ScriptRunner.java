package com.syngleton.chartomancy.service.devtools;

import com.syngleton.chartomancy.controller.DataController;
import com.syngleton.chartomancy.controller.PatternController;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ScriptRunner implements Runnable {

    private final DataController dataController;
    private final PatternController patternController;

    public ScriptRunner(DataController dataController,
                        PatternController patternController) {
        this.dataController = dataController;
        this.patternController = patternController;
    }

    @Override
    public void run() {
        log.info("*** SCRIPT LAUNCHED ***");
        dataController.load("./data/Bitfinex_BTCUSD_d.csv");
        patternController.create();
        patternController.printPatterns();
    }
}
