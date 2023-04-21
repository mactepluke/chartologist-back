package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.controller.DataController;
import com.syngleton.chartomancy.controller.PatternController;
import com.syngleton.chartomancy.data.AppData;
import com.syngleton.chartomancy.view.InteractiveShell;
import org.springframework.stereotype.Service;

@Service
public class ShellService {

    public boolean launchShell(DataController dataController, PatternController patternController, AppData appData)   {
        Thread interactiveShell = new Thread(new InteractiveShell(dataController, patternController, appData));
        interactiveShell.start();
        return true;
    }

}
