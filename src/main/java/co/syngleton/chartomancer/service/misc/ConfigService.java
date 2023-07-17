package co.syngleton.chartomancer.service.misc;

import co.syngleton.chartomancer.analytics.ComputationSettings;
import co.syngleton.chartomancer.analytics.ComputationType;
import co.syngleton.chartomancer.data.CoreData;
import co.syngleton.chartomancer.factory.PatternSettings;
import co.syngleton.chartomancer.model.charting.misc.PatternType;
import co.syngleton.chartomancer.service.domain.DataService;
import co.syngleton.chartomancer.service.domain.PatternService;
import co.syngleton.chartomancer.service.domain.TradingService;
import co.syngleton.chartomancer.util.Check;
import co.syngleton.chartomancer.util.Format;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class ConfigService {

    private static final String CORE_DATA_ARCHIVES_FOLDER_PATH = "./archives/Core_Data_archive_";
    private static final String NEW_LINE = System.getProperty("line.separator");
    private final LaunchService launchService;
    private final DataService dataService;
    private final PatternService patternService;
    private final TradingService tradingService;

    @Autowired
    public ConfigService(DataService dataService,
                         PatternService patternService,
                         LaunchService launchService,
                         TradingService tradingService) {
        this.dataService = dataService;
        this.patternService = patternService;
        this.launchService = launchService;
        this.tradingService = tradingService;
    }

    public CoreData initializeCoreData(String dataFolderName,
                                       List<String> dataFilesNames,
                                       boolean runAnalysisAtStartup,
                                       boolean generateTradingData,
                                       boolean createGraphsForMissingTimeframes,
                                       boolean loadCoreDataAtStartup,
                                       boolean overrideSavedCoreData,
                                       boolean createTimestampedCoreDataArchive,
                                       PurgeOption purgeAfterTradingDataGeneration,
                                       PatternSettings.Autoconfig patternSettingsAutoconfig,
                                       ComputationSettings.Autoconfig computationSettings,
                                       ComputationType computationType,
                                       PatternType computablePatternType,
                                       boolean atomicPartition,
                                       boolean fullScope,
                                       boolean launchAutomation
    ) {

        CoreData coreData = new CoreData();

        log.info(NEW_LINE +
                        "INITIALIZING CORE DATA (pattern settings={}, , pattern type={}, computation={}, computation settings={})",
                patternSettingsAutoconfig,
                computablePatternType,
                computationType,
                computationSettings);

        log.debug(NEW_LINE +
                        "Initialization parameters:" + NEW_LINE +
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
                        "Launching automation after analysis: {}",
                dataFolderName,
                dataFilesNames,
                runAnalysisAtStartup,
                generateTradingData,
                createGraphsForMissingTimeframes,
                loadCoreDataAtStartup,
                overrideSavedCoreData,
                purgeAfterTradingDataGeneration,
                patternSettingsAutoconfig,
                computationSettings,
                computationType,
                computablePatternType,
                atomicPartition,
                fullScope,
                launchAutomation);

        //LOADING TRADING DATA IF APPLICABLE
        log.info("Loaded core data: {}",
                Check.executeIfTrue(loadCoreDataAtStartup,
                        dataService::loadCoreData,
                        coreData));
        //RUNNING ANALYSIS IF APPLICABLE
        if (runAnalysisAtStartup) {
            log.info("Performed data analysis: {}", runAnalysis(
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

            if (generateTradingData && loadCoreDataAtStartup) {
                log.warn("Trading data loaded from file will be overriden by newly generated trading data.");
            }

            log.info("Saved core data overriden with newly generated core data: {}",
                    Check.executeIfTrue(overrideSavedCoreData, dataService::saveCoreData, coreData));

            boolean result = false;

            if (createTimestampedCoreDataArchive) {
                result = dataService.saveCoreDataWithName(coreData,
                        CORE_DATA_ARCHIVES_FOLDER_PATH +
                                "_" +
                                Format.toFileNameCompatibleDateTime(coreData.getPatternSettings().getComputationDate())
                );
            }
            log.info("Created time stamped archive with newly generated data: {}", result);
        }
        //GENERATING TRADING DATA
        if (generateTradingData) {
            log.info("Generated trading data: {}", dataService.generateTradingData(coreData));

            log.info("Purged non-trading data: {}",
                    dataService.purgeNonTradingData(coreData, purgeAfterTradingDataGeneration));
        } else if (purgeAfterTradingDataGeneration != PurgeOption.NO) {
            log.warn("Non-trading data will not be purged as no trading data has been generated.");
        }
        //PRINTING LAUNCHING AUTOMATION IF APPLICABLE
        if (launchAutomation) {
            launchService.launchAutomation(coreData, dataService, patternService, tradingService);
        }

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

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

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

        stopWatch.stop();

        log.info("Analysis time: {} seconds.", TimeUnit.MILLISECONDS.toSeconds(stopWatch.getLastTaskTimeMillis()));

        return Check.notNullNotEmpty(coreData.getPatternBoxes());
    }
}
