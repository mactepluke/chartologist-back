package com.syngleton.chartomancy.view;

import com.syngleton.chartomancy.controller.DataController;
import com.syngleton.chartomancy.controller.PatternController;
import com.syngleton.chartomancy.data.UserSessionData;
import com.syngleton.chartomancy.model.User;
import com.syngleton.chartomancy.dto.PatternSettingsDTO;
import com.syngleton.chartomancy.model.PatternType;
import com.syngleton.chartomancy.factory.PatternSettings;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ScriptRunner implements Runnable {

    private final DataController dataController;
    private final PatternController patternController;
    private final User devToolsUser;

    public ScriptRunner(DataController dataController,
                        PatternController patternController,
                        User devToolsUser) {
        this.dataController = dataController;
        this.patternController = patternController;
        this.devToolsUser = devToolsUser;
    }

    @Override
    public void run() {
        log.info("*** SCRIPT LAUNCHED ***");
        devToolsUser.setUserSessionData(new UserSessionData());
        devToolsUser.getUserSessionData().setGraph(dataController.load("./data/Bitfinex_BTCUSD_d.csv").getBody());
        devToolsUser.getUserSessionData().setPatterns(
                patternController.create(new PatternSettingsDTO(PatternType.PREDICTIVE,
                PatternSettings.Autoconfig.USE_DEFAULTS, "Script Runner"),
                        devToolsUser
        ).getBody());
        devToolsUser.getUserSessionData().setPatterns(patternController.compute(devToolsUser).getBody());
        patternController.printPatterns(devToolsUser);
    }
}
