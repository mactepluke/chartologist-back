package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.CoreDataSettingNames;
import co.syngleton.chartomancer.core_entities.DefaultCoreData;
import co.syngleton.chartomancer.core_entities.PurgeOption;
import co.syngleton.chartomancer.pattern_recognition.ComputationSettings;
import co.syngleton.chartomancer.pattern_recognition.PatternComputer;
import co.syngleton.chartomancer.pattern_recognition.PatternSettings;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;


@Configuration
@Log4j2
@AllArgsConstructor
class DataConfig {
    public static final String CORE_DATA_ARCHIVES_FOLDER_PATH = "./archives/Core_Data_archive";
    public static final String TEST_CORE_DATA_FILE_PATH = "./core_data/TEST_coredata.ser";
    private static final String NEW_LINE = System.lineSeparator();
    private static final String DELIMITER = "/";
    private final DataProcessor dataProcessor;
    private final PatternComputer patternComputer;
    private final DataProperties dataProperties;

    @Bean
    CoreData coreData() {

        CoreData coreData = DefaultCoreData.newInstance();

        logInitializationProperties();
        preLoadData(coreData);
        performAnalysis(coreData);
        generateTradingData(coreData);
        saveCoreData(coreData);

        log.debug(coreData);

        return coreData;
    }

    private void logInitializationProperties() {

        log.info(NEW_LINE +
                        "INITIALIZING CORE DATA (pattern settings={}, , pattern type={}, computation={}, computation settings={})",
                dataProperties.patternSettingsAutoconfig(),
                dataProperties.computablePatternType(),
                dataProperties.computationType(),
                dataProperties.computationSettingsAutoconfig());

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
                        "Atomic graph partition: {}" + NEW_LINE +
                        "Full scope prediction range: {}",
                dataProperties.folderName(),
                dataProperties.filesNames(),
                dataProperties.runAnalysisAtStartup(),
                dataProperties.generateTradingData(),
                dataProperties.createGraphsForMissingTimeframes(),
                dataProperties.loadCoreDataAtStartup(),
                dataProperties.overrideSavedCoreData(),
                dataProperties.purgeAfterTradingDataGeneration(),
                dataProperties.patternSettingsAutoconfig(),
                dataProperties.computationSettingsAutoconfig(),
                dataProperties.atomicPartition(),
                dataProperties.fullScope());
    }

    private void preLoadData(CoreData coreData) {
        log.info("Loaded core data: {}",
                dataProperties.loadCoreDataAtStartup()
                        && dataProcessor.loadCoreData(coreData, dataProperties.sourceName()));
    }

    private void performAnalysis(CoreData coreData) {
        if (dataProperties.runAnalysisAtStartup()) {
            log.info("Performed data analysis: {}", runAnalysis(coreData));

            if (dataProperties.generateTradingData() && dataProperties.loadCoreDataAtStartup()) {
                log.warn("Trading data loaded from file will be overriden by newly generated trading data.");
            }

            boolean result = false;

            if (dataProperties.createTimestampedCoreDataArchive()) {
                result = dataProcessor.saveCoreData(coreData,
                        CORE_DATA_ARCHIVES_FOLDER_PATH +
                                "_" +
                                coreData.getPatternSetting(CoreDataSettingNames.COMPUTATION_DATE.name())
                );
            }
            log.info("Created time stamped archive with newly generated data: {}", result);
        }
    }

    private boolean runAnalysis(CoreData coreData) {

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        //LOADING GRAPHS
        log.debug("Loading graphs from folder {} with files {}...", dataProperties.folderName(), dataProperties.filesNames());
        if (dataProcessor.loadGraphs(coreData, dataProperties.folderName(), dataProperties.filesNames())) {
            log.info("Created {} graph(s)", coreData.getGraphNumber());
        } else {
            log.error("Application could not initialize its data: no files of correct format could be read.");
        }
        //CREATING GRAPHS FOR MISSING TIMEFRAMES
        log.debug("Creating graphs for missing timeframes...");
        log.info("Created graphs for missing timeframes: {}",
                dataProperties.createGraphsForMissingTimeframes() && dataProcessor.createGraphsForMissingTimeframes(coreData));


        //CREATING PREDICTIVE PATTERNS
        log.info("Creating pattern boxes...");
        PatternSettings.Builder patternSettingsInput = new PatternSettings.Builder()
                .patternType(dataProperties.computablePatternType())
                .autoconfig(dataProperties.patternSettingsAutoconfig());
        if (dataProperties.fullScope()) {
            patternSettingsInput = patternSettingsInput.scope("FULL");
        }

        if (dataProperties.atomicPartition()) {
            patternSettingsInput = patternSettingsInput.atomizePartition();
        }

        if (dataProcessor.createPatternsForCoreData(coreData, patternSettingsInput)) {
            log.info("Created {} pattern box(es)", coreData.getNumberOfPatternSets());
        } else {
            log.error("Application could not initialize its data: no pattern boxes could be created.");
        }
        //COMPUTING PREDICTIVE PATTERNS
        log.debug("Computing pattern boxes...");
        ComputationSettings.Builder computationSettingsInput = new ComputationSettings.Builder()
                .computationType(dataProperties.computationType())
                .autoconfig(dataProperties.computationSettingsAutoconfig());

        if (patternComputer.computeCoreData(coreData, computationSettingsInput)) {
            log.info("Computed {} pattern box(es)", coreData.getNumberOfPatternSets());
        } else {
            log.error("Application could not initialize its data: no pattern boxes format could be computed.");
        }

        stopWatch.stop();

        log.info("Analysis time: {} seconds.", TimeUnit.MILLISECONDS.toSeconds(stopWatch.getLastTaskTimeMillis()));

        return coreData.getNumberOfPatternSets() > 0;
    }

    private void generateTradingData(CoreData coreData) {
        if (dataProperties.generateTradingData()) {
            log.info("Generated trading data: {}", coreData.pushTradingPatternData());

            log.info("Purged non-trading data: {}",
                    coreData.purgeUselessData(dataProperties.purgeAfterTradingDataGeneration()));
        } else if (dataProperties.purgeAfterTradingDataGeneration() != PurgeOption.NO) {
            log.warn("Non-trading data will not be purged as no trading data has been generated.");
        }
    }

    private void saveCoreData(CoreData coreData) {
        log.info("Saved core data overriden with newly generated core data: {}",
                dataProperties.overrideSavedCoreData()
                        && dataProcessor.saveCoreData(coreData, dataProperties.sourceName()));
        log.info("Saved test core data overriden with newly generated core data: {}",
                dataProperties.overrideSavedTestCoreData()
                        && dataProcessor.saveCoreData(coreData, TEST_CORE_DATA_FILE_PATH));
    }

}
