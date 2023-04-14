package com.syngleton.chartomancy.devtools;

import com.syngleton.chartomancy.controller.DataController;
import com.syngleton.chartomancy.controller.PatternController;
import com.syngleton.chartomancy.data.UserSessionData;
import com.syngleton.chartomancy.model.User;
import com.syngleton.chartomancy.dto.PatternSettingsDTO;
import com.syngleton.chartomancy.model.patterns.PatternType;
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
        devToolsuser.setUserSessionData(new UserSessionData());
        devToolsuser.getUserSessionData().setGraph(dataController.load("./data/Bitfinex_BTCUSD_d.csv").getBody());
        devToolsuser.getUserSessionData().setPatterns(
                patternController.create(new PatternSettingsDTO(PatternType.BASIC,
                PatternSettings.Autoconfig.USE_DEFAULTS, "Script Runner"),
                        devToolsuser
        ).getBody());
        patternController.printPatterns(devToolsuser);
    }
}
