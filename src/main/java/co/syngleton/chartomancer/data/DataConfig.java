package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.pattern_recognition.*;
import co.syngleton.chartomancer.shared_constants.CoreDataSettingNames;
import co.syngleton.chartomancer.shared_domain.CoreData;
import co.syngleton.chartomancer.shared_domain.DefaultCoreData;
import co.syngleton.chartomancer.util.Check;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static co.syngleton.chartomancer.shared_constants.Misc.*;

@Configuration
@Log4j2
class DataConfig {
    private final DataProcessor dataProcessor;
    private final PatternGenerator patternGenerator;
    private final PatternComputer patternComputer;
    @Value("${data_folder_name:data}")
    private String dataFolderName;
    @Value("#{'${data_files_names}'.split(',')}")
    private List<String> dataFilesNames;
    @Value("${run_analysis_at_startup:false}")
    private boolean runAnalysisAtStartup;
    @Value("${generate_trading_data:false}")
    private boolean generateTradingData;
    @Value("${create_graphs_for_missing_timeframes:false}")
    private boolean createGraphsForMissingTimeframes;
    @Value("${load_core_data_at_startup:false}")
    private boolean loadCoreDataAtStartup;
    @Value("${override_saved_core_data:false}")
    private boolean overrideSavedCoreData;
    @Value("${override_saved_test_core_data:false}")
    private boolean overrideSavedTestCoreData;
    @Value("${create_timestamped_core_data_archive:false}")
    private boolean createTimestampedCoreDataArchive;
    @Value("${purge_after_trading_data_generation:NO}")
    private PurgeOption purgeAfterTradingDataGeneration;
    @Value("${pattern_settings_autoconfig:DEFAULT}")
    private PatternSettings.Autoconfig patternSettingsAutoconfig;
    @Value("${computation_settings_autoconfig:DEFAULT}")
    private ComputationSettings.Autoconfig computationSettings;
    @Value("${computation_type:BASIC_ITERATON}")
    private ComputationType computationType;
    @Value("${computable_pattern_type:LIGHT_PREDICTIVE}")
    private PatternSettings.PatternType computablePatternType;
    @Value("${atomic_partition:false}")
    private boolean atomicPartition;
    @Value("${full_scope:false}")
    private boolean fullScope;

    @Autowired
    DataConfig(DataProcessor dataProcessor,
               PatternGenerator patternGenerator,
               PatternComputer patternComputer) {
        this.dataProcessor = dataProcessor;
        this.patternGenerator = patternGenerator;
        this.patternComputer = patternComputer;
    }

    //TODO Refactor to split the initialization into smaller methods
    @Bean
    CoreData coreData() {

        CoreData coreData = new DefaultCoreData();

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
                        "Full scope prediction range: {}",
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
                fullScope);

        //LOADING TRADING DATA IF APPLICABLE
        log.info("Loaded core data: {}", loadCoreDataAtStartup && dataProcessor.loadCoreData(coreData));
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

            boolean result = false;

            if (createTimestampedCoreDataArchive) {
                result = dataProcessor.saveCoreDataWithName(coreData,
                        CORE_DATA_ARCHIVES_FOLDER_PATH +
                                "_" +
                                coreData.getPatternSettings().get(CoreDataSettingNames.COMPUTATION_DATE)
                );
            }
            log.info("Created time stamped archive with newly generated data: {}", result);
        }
        //GENERATING TRADING DATA
        if (generateTradingData) {
            log.info("Generated trading data: {}", dataProcessor.generateTradingData(coreData));

            log.info("Purged non-trading data: {}",
                    dataProcessor.purgeUselessData(coreData, purgeAfterTradingDataGeneration));
        } else if (purgeAfterTradingDataGeneration != PurgeOption.NO) {
            log.warn("Non-trading data will not be purged as no trading data has been generated.");
        }

        //SAVING CORE DATA
        log.info("Saved core data overriden with newly generated core data: {}", overrideSavedCoreData && dataProcessor.saveCoreData(coreData));

        log.info("Saved test core data overriden with newly generated core data: {}",
                overrideSavedTestCoreData && dataProcessor.saveCoreDataWithName(coreData, TEST_CORE_DATA_FILENAME));

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

        if (patternGenerator.createPatternBoxes(coreData, patternSettingsInput)) {
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
