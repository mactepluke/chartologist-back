package com.syngleton.chartomancy.service.devtools;

import com.syngleton.chartomancy.controller.DataController;
import com.syngleton.chartomancy.controller.PatternController;
import com.syngleton.chartomancy.dto.PatternSettingsDTO;
import com.syngleton.chartomancy.model.patterns.PatternTypes;
import com.syngleton.chartomancy.service.patterns.PatternSettings;
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
        patternController.create(new PatternSettingsDTO(PatternTypes.BASIC,
                PatternSettings.Autoconfig.USE_DEFAULTS,
                0,0
        ));
        patternController.printPatterns();
    }
}
