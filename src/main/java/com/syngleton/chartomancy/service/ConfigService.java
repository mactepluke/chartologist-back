package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.analytics.ComputationSettings;
import com.syngleton.chartomancy.analytics.ComputationType;
import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.factory.PatternSettings;
import com.syngleton.chartomancy.model.charting.patterns.PatternType;
import com.syngleton.chartomancy.util.Check;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class ConfigService {

    private final DataService dataService;
    private final PatternService patternService;

    @Autowired
    public ConfigService(DataService dataService,
                         PatternService patternService) {
        this.dataService = dataService;
        this.patternService = patternService;
    }

    public CoreData initializeCoreData(String dataFolderName,
                                       List<String> dataFilesNames,
                                       boolean runAnalysisAtStartup,
                                       boolean generateTradingData,
                                       boolean createGraphsForMissingTimeframes,
                                       boolean loadTradingDataAtStartup,
                                       boolean overrideSavedTradingData,
                                       boolean purgeAfterTradingDataGeneration,
                                       PatternSettings.Autoconfig patternSettingsAutoconfig,
                                       ComputationSettings.Autoconfig computationSettings,
                                       ComputationType computationType,
                                       boolean fullScope,
                                       boolean printCoreData
    ) {

        CoreData coreData = new CoreData();

        log.info("INITIALIZING CORE DATA WITH: PATTERN SETTINGS={}, COMPUTATION={}, COMPUTATION SETTINGS={}", patternSettingsAutoconfig, computationType, computationSettings);

        //RUNNING ANALYSIS IF APPLICABLE
        if (runAnalysisAtStartup) {
            log.debug("Performed data analysis: {}", runAnalysis(
                    coreData,
                    dataFolderName,
                    dataFilesNames,
                    patternSettingsAutoconfig,
                    computationSettings,
                    computationType,
                    createGraphsForMissingTimeframes,
                    fullScope));



            if (generateTradingData) {
                if (loadTradingDataAtStartup)    {
                    log.warn("Conflict in data config parameters: trading data will be generated, not loaded from file.");
                    loadTradingDataAtStartup = false;
                }
                log.info("Generated trading data: {}", dataService.generateTradingData(coreData));
                if (overrideSavedTradingData) {
                    log.info("Overriden saved trading data with newly generated data: {}", dataService.saveTradingData(coreData));
                }
                if (purgeAfterTradingDataGeneration) {
                    log.info("Purged non-trading data: {}", dataService.purgeNonTradingData(coreData));
                }
            }
        }

        //LOADING TRADING DATA IF APPLICABLE
        if (loadTradingDataAtStartup && (!runAnalysisAtStartup || !overrideSavedTradingData)) {
            log.info("Loaded trading data: {}", dataService.loadTradingData(coreData));
        }

        //PRINTING CORE DATA CONTENTS IF APPLICABLE
        if (printCoreData) {
            dataService.printCoreData(coreData);
        }

        return coreData;
    }

    private boolean runAnalysis(CoreData coreData,
                                String dataFolderName,
                                List<String> dataFilesNames,
                                PatternSettings.Autoconfig patternSettingsAutoconfig,
                                ComputationSettings.Autoconfig computationSettings,
                                ComputationType computationType,
                                boolean createGraphsForMissingTimeframes,
                                boolean fullScope) {

        //LOADING GRAPHS
        if (dataService.loadGraphs(coreData, dataFolderName, dataFilesNames)) {
            log.info("Created {} graph(s)", coreData.getGraphs().size());
        } else {
            log.error("Application could not initialize its data: no files of correct format could be read.");
        }

        //CREATING GRAPHS FOR MISSING TIMEFRAMES
        if (createGraphsForMissingTimeframes) {
            log.info("Created graphs for missing timeframes: {}", dataService.createGraphsForMissingTimeframes(coreData));
        }

        //CREATING PREDICTIVE PATTERNS
        PatternSettings.Builder patternSettingsInput = new PatternSettings.Builder()
                .patternType(PatternType.PREDICTIVE)
                .autoconfig(patternSettingsAutoconfig);
        if (fullScope) {
            patternSettingsInput = patternSettingsInput.scope("FULL");
        }

        if (patternService.createPatternBoxes(coreData, patternSettingsInput)) {
            log.info("Created {} pattern box(es)", coreData.getPatternBoxes().size());
        } else {
            log.error("Application could not initialize its data: no pattern boxes could be created.");
        }

        //COMPUTING PREDICTIVE PATTERNS
        ComputationSettings.Builder computationSettingsInput = new ComputationSettings.Builder()
                .computationType(computationType)
                .autoconfig(computationSettings);

        if (patternService.computePatternBoxes(coreData, computationSettingsInput)) {
            log.info("Computed {} pattern box(es)", coreData.getPatternBoxes().size());
        } else {
            log.error("Application could not initialize its data: no pattern boxes format could be computed.");
        }

        return Check.notNullNotEmpty(coreData.getPatternBoxes());
    }
}
