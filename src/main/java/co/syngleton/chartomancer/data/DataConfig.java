package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.pattern_recognition.ComputationSettings;
import co.syngleton.chartomancer.pattern_recognition.ComputationType;
import co.syngleton.chartomancer.pattern_recognition.PatternComputer;
import co.syngleton.chartomancer.pattern_recognition.PatternSettings;
import co.syngleton.chartomancer.shared_constants.CoreDataSettingNames;
import co.syngleton.chartomancer.shared_domain.CoreData;
import co.syngleton.chartomancer.shared_domain.DefaultCoreData;
import co.syngleton.chartomancer.util.Check;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static co.syngleton.chartomancer.shared_constants.Misc.CORE_DATA_ARCHIVES_FOLDER_PATH;
import static co.syngleton.chartomancer.shared_constants.Misc.TEST_CORE_DATA_FILENAME;

@Configuration
@Log4j2
@AllArgsConstructor
class DataConfig {
    private static final String NEW_LINE = System.lineSeparator();
    private final DataProcessor dataProcessor;
    private final PatternComputer patternComputer;
    private final DataProperties dataProperties;

    //TODO Refactor to split the initialization into smaller methods
    @Bean
    CoreData coreData() {

        CoreData coreData = new DefaultCoreData();

        log.info(NEW_LINE +
                        "INITIALIZING CORE DATA (pattern settings={}, , pattern type={}, computation={}, computation settings={})",
                dataProperties.getPatternSettingsAutoconfig(),
                dataProperties.getComputablePatternType(),
                dataProperties.getComputationType(),
                dataProperties.getComputationSettingsAutoconfig());

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
                        "Full scope prediction range: {}",
                dataProperties.getFolderName(),
                dataProperties.getFilesNames(),
                dataProperties.isRunAnalysisAtStartup(),
                dataProperties.isGenerateTradingData(),
                dataProperties.isCreateGraphsForMissingTimeframes(),
                dataProperties.isLoadCoreDataAtStartup(),
                dataProperties.isOverrideSavedCoreData(),
                dataProperties.getPurgeAfterTradingDataGeneration(),
                dataProperties.getPatternSettingsAutoconfig(),
                dataProperties.getComputationSettingsAutoconfig(),
                dataProperties.getComputationType(),
                dataProperties.getComputablePatternType(),
                dataProperties.isAtomicPartition(),
                dataProperties.isFullScope());

        //LOADING TRADING DATA IF APPLICABLE
        log.info("Loaded core data: {}", dataProperties.isLoadCoreDataAtStartup() && dataProcessor.loadCoreData(coreData, dataProperties.getSourceName()));
        //RUNNING ANALYSIS IF APPLICABLE
        if (dataProperties.isRunAnalysisAtStartup()) {
            log.info("Performed data analysis: {}", runAnalysis(
                    coreData,
                    dataProperties.getFolderName(),
                    dataProperties.getFilesNames(),
                    dataProperties.getPatternSettingsAutoconfig(),
                    dataProperties.getComputationSettingsAutoconfig(),
                    dataProperties.getComputationType(),
                    dataProperties.getComputablePatternType(),
                    dataProperties.isCreateGraphsForMissingTimeframes(),
                    dataProperties.isAtomicPartition(),
                    dataProperties.isFullScope()));

            if (dataProperties.isGenerateTradingData() && dataProperties.isLoadCoreDataAtStartup()) {
                log.warn("Trading data loaded from file will be overriden by newly generated trading data.");
            }

            boolean result = false;

            if (dataProperties.isCreateTimestampedCoreDataArchive()) {
                result = dataProcessor.saveCoreData(coreData,
                        CORE_DATA_ARCHIVES_FOLDER_PATH +
                                "_" +
                                coreData.getPatternSettings().get(CoreDataSettingNames.COMPUTATION_DATE)
                );
            }
            log.info("Created time stamped archive with newly generated data: {}", result);
        }
        //GENERATING TRADING DATA
        if (dataProperties.isGenerateTradingData()) {
            log.info("Generated trading data: {}", dataProcessor.generateTradingData(coreData));

            log.info("Purged non-trading data: {}",
                    dataProcessor.purgeUselessData(coreData, dataProperties.getPurgeAfterTradingDataGeneration()));
        } else if (dataProperties.getPurgeAfterTradingDataGeneration() != PurgeOption.NO) {
            log.warn("Non-trading data will not be purged as no trading data has been generated.");
        }

        //SAVING CORE DATA
        log.info("Saved core data overriden with newly generated core data: {}", dataProperties.isOverrideSavedCoreData() && dataProcessor.saveCoreData(coreData, dataProperties.getSourceName()));

        log.info("Saved test core data overriden with newly generated core data: {}",
                dataProperties.isOverrideSavedTestCoreData() && dataProcessor.saveCoreData(coreData, TEST_CORE_DATA_FILENAME));

        return coreData;
    }


    private boolean runAnalysis(CoreData coreData,
                                String dataFolderName,
                                List<String> dataFilesNames,
                                PatternSettings.Autoconfig patternSettingsAutoconfig,
                                ComputationSettings.Autoconfig computationSettings,
                                ComputationType computationType,
                                PatternSettings.PatternType computablePatternType,
                                boolean createGraphsForMissingTimeframes,
                                boolean atomicPartition,
                                boolean fullScope) {

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        //LOADING GRAPHS
        log.debug("Loading graphs from folder {} with files {}...", dataFolderName, dataFilesNames);
        if (dataProcessor.loadGraphs(coreData, dataFolderName, dataFilesNames)) {
            log.info("Created {} graph(s)", coreData.getGraphs().size());
        } else {
            log.error("Application could not initialize its data: no files of correct format could be read.");
        }
        //CREATING GRAPHS FOR MISSING TIMEFRAMES
        log.debug("Creating graphs for missing timeframes...");
        log.info("Created graphs for missing timeframes: {}",
                createGraphsForMissingTimeframes && dataProcessor.createGraphsForMissingTimeframes(coreData));


        //CREATING PREDICTIVE PATTERNS
        log.info("Creating pattern boxes...");
        PatternSettings.Builder patternSettingsInput = new PatternSettings.Builder()
                .patternType(computablePatternType)
                .autoconfig(patternSettingsAutoconfig);
        if (fullScope) {
            patternSettingsInput = patternSettingsInput.scope("FULL");
        }

        if (atomicPartition) {
            patternSettingsInput = patternSettingsInput.atomizePartition();
        }

        if (dataProcessor.createPatternBoxes(coreData, patternSettingsInput)) {
            log.info("Created {} pattern box(es)", coreData.getPatternBoxes().size());
        } else {
            log.error("Application could not initialize its data: no pattern boxes could be created.");
        }
        //COMPUTING PREDICTIVE PATTERNS
        log.debug("Computing pattern boxes...");
        ComputationSettings.Builder computationSettingsInput = new ComputationSettings.Builder()
                .computationType(computationType)
                .autoconfig(computationSettings);

        if (patternComputer.computePatternBoxes(coreData, computationSettingsInput)) {
            log.info("Computed {} pattern box(es)", coreData.getPatternBoxes().size());
        } else {
            log.error("Application could not initialize its data: no pattern boxes format could be computed.");
        }

        stopWatch.stop();

        log.info("Analysis time: {} seconds.", TimeUnit.MILLISECONDS.toSeconds(stopWatch.getLastTaskTimeMillis()));

        return Check.isNotEmpty(coreData.getPatternBoxes());
    }

}
