package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.controller.root.DataController;
import com.syngleton.chartomancy.controller.root.PatternController;
import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.view.InteractiveShell;
import org.springframework.stereotype.Service;

@Service
public class ShellService {

    public boolean launchShell(DataController dataController, PatternController patternController, CoreData coreData)   {
        Thread interactiveShell = new Thread(new InteractiveShell(dataController, patternController, coreData));
        interactiveShell.start();
        return true;
    }

}
