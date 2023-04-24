package com.syngleton.chartomancy.configuration;

import com.syngleton.chartomancy.analytics.ComputationSettings;
import com.syngleton.chartomancy.analytics.ComputationType;
import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.factory.PatternSettings;
import com.syngleton.chartomancy.service.ConfigService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Log4j2
@Configuration
public class DataConfig {
//TODO check properties for data names
    @Value("${data_folder_name}")
    private String dataFolderName;
    @Value("#{'${data_files_names}'.split(',')}")
    private List<String> dataFilesNames;
    @Value("${load_trading_data_at_startup}")
    private boolean runAnalysisAtStartup;
    @Value("${generate_trading_data}")
    private boolean generateTradingData;
    @Value("${create_graphs_for_missing_timeframes}")
    private boolean createGraphsForMissingTimeframes;
    @Value("${run_analysis_at_startup}")
    private boolean loadTradingDataAtStartup;
    @Value("${override_saved_trading_data}")
    private boolean overrideSavedTradingData;
    @Value("${purge_after_trading_data_generation}")
    private boolean purgeAfterTradingDataGeneration;
    @Value("${pattern_settings_autoconfig}")
    private PatternSettings.Autoconfig patternSettingsAutoconfig;
    @Value("${computation_settings_autoconfig}")
    private ComputationSettings.Autoconfig computationSettingsAutoconfig;
    @Value("${computation_type}")
    private ComputationType computationType;
    @Value("${full_scope}")
    private boolean fullScope;
    @Value("${print_core_data}")
    private boolean printCoreData;

    private final ConfigService configService;

    @Autowired
    public DataConfig(ConfigService configService) {
        this.configService = configService;
    }

    @Bean
    CoreData coreData() {
        return configService.initializeCoreData(
                dataFolderName,
                dataFilesNames,
                runAnalysisAtStartup,
                generateTradingData,
                createGraphsForMissingTimeframes,
                loadTradingDataAtStartup,
                overrideSavedTradingData,
                purgeAfterTradingDataGeneration,
                patternSettingsAutoconfig,
                computationSettingsAutoconfig,
                computationType,
                fullScope,
                printCoreData
        );
    }
}
