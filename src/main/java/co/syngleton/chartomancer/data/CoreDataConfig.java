package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.domain.CoreData;
import co.syngleton.chartomancer.pattern_recognition.ComputationSettings;
import co.syngleton.chartomancer.pattern_recognition.ComputationType;
import co.syngleton.chartomancer.pattern_recognition.PatternSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CoreDataConfig {
    private final DataInitializer initializationService;
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
    private ComputationSettings.Autoconfig computationSettingsAutoconfig;
    @Value("${computation_type:BASIC_ITERATON}")
    private ComputationType computationType;
    @Value("${computable_pattern_type:LIGHT_PREDICTIVE}")
    private PatternSettings.PatternType computablePatternType;
    @Value("${atomic_partition:false}")
    private boolean atomicPartition;
    @Value("${full_scope:false}")
    private boolean fullScope;
    @Value("${launch_automation:false}")
    private boolean launchAutomation;

    @Autowired
    CoreDataConfig(DataInitializer initializationService) {
        this.initializationService = initializationService;
    }

    @Bean
    CoreData coreData() {
        return initializationService.initializeCoreData(
                dataFolderName,
                dataFilesNames,
                runAnalysisAtStartup,
                generateTradingData,
                createGraphsForMissingTimeframes,
                loadCoreDataAtStartup,
                overrideSavedCoreData,
                overrideSavedTestCoreData,
                createTimestampedCoreDataArchive,
                purgeAfterTradingDataGeneration,
                patternSettingsAutoconfig,
                computationSettingsAutoconfig,
                computationType,
                computablePatternType,
                atomicPartition,
                fullScope,
                launchAutomation
        );
    }
}
