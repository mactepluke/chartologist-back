package com.syngleton.chartomancy.devtools;

import com.syngleton.chartomancy.controller.DataController;
import com.syngleton.chartomancy.controller.PatternController;
import com.syngleton.chartomancy.model.User;
import org.springframework.stereotype.Service;

@Service
public class DevToolsService {

    public boolean launchShell(DataController dataController, PatternController patternController, User devToolsUser)   {
        Thread interactiveShell = new Thread(new InteractiveShell(dataController, patternController, devToolsUser));
        interactiveShell.start();
        return true;
    }

    public boolean runScript(DataController dataController, PatternController patternController, User devToolsUser) {
        Thread scriptRunner = new Thread(new ScriptRunner(dataController, patternController, devToolsUser));
        scriptRunner.start();
        return true;
    }

}
