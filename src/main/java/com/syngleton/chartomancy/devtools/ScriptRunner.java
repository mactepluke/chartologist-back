package com.syngleton.chartomancy.devtools;

import com.syngleton.chartomancy.controller.DataController;
import com.syngleton.chartomancy.controller.PatternController;
import com.syngleton.chartomancy.data.GenericData;
import com.syngleton.chartomancy.model.User;
import com.syngleton.chartomancy.model.patterns.PatternSettingsDTO;
import com.syngleton.chartomancy.model.patterns.PatternTypes;
import com.syngleton.chartomancy.service.patterns.PatternSettings;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ScriptRunner implements Runnable {

    private final DataController dataController;
    private final PatternController patternController;
    private final User devToolsuser;

    public ScriptRunner(DataController dataController,
                        PatternController patternController,
                        User devToolsuser) {
        this.dataController = dataController;
        this.patternController = patternController;
        this.devToolsuser = devToolsuser;
    }

    @Override
    public void run() {
        log.info("*** SCRIPT LAUNCHED ***");
        devToolsuser.setGenericData(new GenericData());
        devToolsuser.getGenericData().setGraph(dataController.load("./data/Bitfinex_BTCUSD_d.csv").getBody());
        devToolsuser.getGenericData().setPatterns(
                patternController.create(new PatternSettingsDTO(PatternTypes.BASIC,
                PatternSettings.Autoconfig.USE_DEFAULTS, "Script Runner"),
                        devToolsuser
        ).getBody());
        patternController.printPatterns(devToolsuser.getGenericData().getPatterns());
    }
}
