package com.syngleton.chartomancy.service.devtools;

import com.syngleton.chartomancy.controller.DataController;
import com.syngleton.chartomancy.controller.PatternController;
import com.syngleton.chartomancy.view.InteractiveShell;
import org.springframework.stereotype.Service;

@Service
public class DevToolsService {

    public boolean launchShell(DataController dataController, PatternController patternController)   {
        Thread interactiveShell = new Thread(new InteractiveShell(dataController, patternController));
        interactiveShell.start();
        return true;
    }

    public boolean runScript(DataController dataController, PatternController patternController) {
        Thread scriptRunner = new Thread(new ScriptRunner(dataController, patternController));
        scriptRunner.start();
        return true;
    }

}
