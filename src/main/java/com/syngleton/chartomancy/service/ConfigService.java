package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.analytics.ComputationSettings;
import com.syngleton.chartomancy.analytics.ComputationType;
import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.factory.PatternSettings;
import com.syngleton.chartomancy.model.charting.misc.Graph;
import com.syngleton.chartomancy.model.charting.misc.Symbol;
import com.syngleton.chartomancy.model.charting.misc.Timeframe;
import com.syngleton.chartomancy.model.charting.patterns.PatternType;
import com.syngleton.chartomancy.model.trading.Trade;
import com.syngleton.chartomancy.util.Check;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class ConfigService {

    private final TradingService tradingService;
    private final DataService dataService;
    private final PatternService patternService;
    private static final String NEW_LINE = System.getProperty("line.separator");

    @Autowired
    public ConfigService(DataService dataService,
                         PatternService patternService,
                         TradingService tradingService) {
        this.dataService = dataService;
        this.patternService = patternService;
        this.tradingService = tradingService;
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
                                       PatternType computablePatternType,
                                       boolean atomicPartition,
                                       boolean fullScope,
                                       boolean printCoreData
    ) {

        CoreData coreData = new CoreData();

        log.info("INITIALIZING CORE DATA (pattern settings={}, , pattern type={}, computation={}, computation settings={})",
                patternSettingsAutoconfig,
                computablePatternType,
                computationType,
                computationSettings);

        log.debug("Initialization parameters:" + NEW_LINE +
                        "Data folder name: {}" + NEW_LINE +
                        "Data file names: {}" + NEW_LINE +
                        "Run analysis at startup: {}" + NEW_LINE +
                        "Generate trading data: {}" + NEW_LINE +
                        "Create graphs for missing timeframes: {}" + NEW_LINE +
                        "Load trading data at startup: {}" + NEW_LINE +
                        "Override saved trading data: {}" + NEW_LINE +
                        "Purge computation data after trading data is generated: {}" + NEW_LINE +
                        "Pattern setting autoconfig: {}" + NEW_LINE +
                        "Computation settings autoconfig: {}" + NEW_LINE +
                        "Computation type: {}" + NEW_LINE +
                        "Computable pattern type: {}" + NEW_LINE +
                        "Atomic graph partition: {}" + NEW_LINE +
                        "Full scope prediction range: {}" + NEW_LINE +
                        "Print core data after analysis: {}" + NEW_LINE,
                dataFolderName,
                dataFilesNames,
                runAnalysisAtStartup,
                generateTradingData,
                createGraphsForMissingTimeframes,
                loadTradingDataAtStartup,
                overrideSavedTradingData,
                purgeAfterTradingDataGeneration,
                patternSettingsAutoconfig,
                computationSettings,
                computationType,
                computablePatternType,
                atomicPartition,
                fullScope,
                printCoreData);

        //RUNNING ANALYSIS IF APPLICABLE
        if (runAnalysisAtStartup) {
            log.debug("Performed data analysis: {}", runAnalysis(
                    coreData,
                    dataFolderName,
                    dataFilesNames,
                    patternSettingsAutoconfig,
                    computationSettings,
                    computationType,
                    computablePatternType,
                    createGraphsForMissingTimeframes,
                    atomicPartition,
                    fullScope));

            if (generateTradingData) {
                if (loadTradingDataAtStartup) {
                    log.warn("Conflict in data config parameters: trading data will be generated, not loaded from file.");
                    loadTradingDataAtStartup = false;
                }
                log.info("Generated trading data: {}", dataService.generateTradingData(coreData));

                log.info("Overriden saved trading data with newly generated data: {}",
                        Check.executeIfTrue(overrideSavedTradingData, dataService::saveTradingData, coreData));

                log.info("Purged non-trading data: {}",
                        Check.executeIfTrue(purgeAfterTradingDataGeneration, dataService::purgeNonTradingData, coreData));

            }
        }
        //LOADING TRADING DATA IF APPLICABLE
        log.info("Loaded trading data: {}",
                Check.executeIfTrue(loadTradingDataAtStartup && (!runAnalysisAtStartup || !overrideSavedTradingData),
                        dataService::loadTradingData,
                        coreData));
        //PRINTING CORE DATA CONTENTS IF APPLICABLE
        Check.executeIfTrue(printCoreData, dataService::printCoreData, coreData);

/*

        //_______________TEST_______________
        Graph graph = coreData.getGraph(Symbol.BTC_USD, Timeframe.HOUR);

        for (var i = 1; i < 3000; i++) {
            Trade trade = tradingService.generateOptimalBasicTrade(graph, coreData, 20 * i, -1, 0);
            if (trade == null) {
                break;
            }
            log.debug("TRADE# {} -------> {}", i + 1, trade);
        }
        //______________________________
*/


        return coreData;
    }

    private boolean runAnalysis(CoreData coreData,
                                String dataFolderName,
                                List<String> dataFilesNames,
                                PatternSettings.Autoconfig patternSettingsAutoconfig,
                                ComputationSettings.Autoconfig computationSettings,
                                ComputationType computationType,
                                PatternType computablePatternType,
                                boolean createGraphsForMissingTimeframes,
                                boolean atomicPartition,
                                boolean fullScope) {

        //LOADING GRAPHS
        if (dataService.loadGraphs(coreData, dataFolderName, dataFilesNames)) {
            log.info("Created {} graph(s)", coreData.getGraphs().size());
        } else {
            log.error("Application could not initialize its data: no files of correct format could be read.");
        }
        //CREATING GRAPHS FOR MISSING TIMEFRAMES
        log.info("Created graphs for missing timeframes: {}",
                Check.executeIfTrue(createGraphsForMissingTimeframes, dataService::createGraphsForMissingTimeframes, coreData));

        //CREATING PREDICTIVE PATTERNS
        PatternSettings.Builder patternSettingsInput = new PatternSettings.Builder()
                .patternType(computablePatternType)
                .autoconfig(patternSettingsAutoconfig);
        if (fullScope) {
            patternSettingsInput = patternSettingsInput.scope("FULL");
        }

        if (atomicPartition) {
            patternSettingsInput = patternSettingsInput.atomizePartition();
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
