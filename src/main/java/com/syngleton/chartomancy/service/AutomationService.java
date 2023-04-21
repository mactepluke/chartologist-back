package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.analytics.ComputationSettings;
import com.syngleton.chartomancy.analytics.ComputationType;
import com.syngleton.chartomancy.data.AppData;
import com.syngleton.chartomancy.factory.PatternSettings;
import com.syngleton.chartomancy.model.charting.PatternType;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class AutomationService {

    private final DataService dataService;
    private final PatternService patternService;

    @Autowired
    public AutomationService(DataService dataService,
                      PatternService patternService) {
        this.dataService = dataService;
        this.patternService = patternService;
    }

    public AppData generateAppData(String dataFolderName, List<String> dataFilesNames)    {

        AppData appData = new AppData();

        appData.setGraphs(dataService.loadGraphs(dataFolderName, dataFilesNames));

        PatternSettings.Builder patternSettingsInput = new PatternSettings.Builder()
                .name("DataConfig")
                .patternType(PatternType.PREDICTIVE)
                .autoconfig(PatternSettings.Autoconfig.USE_DEFAULTS);

        appData.setPatternBoxes(patternService.createPatternBoxes(appData.getGraphs(), patternSettingsInput));

        ComputationSettings.Builder computationSettingsInput = new ComputationSettings.Builder()
                .computationType(ComputationType.BASIC_ITERATION)
                .autoconfig(ComputationSettings.Autoconfig.USE_DEFAULTS);

        appData.setPatternBoxes(patternService.computePatternsList(appData.getPatternBoxes(), appData.getGraphs(), computationSettingsInput));

        return appData;
    }
}
