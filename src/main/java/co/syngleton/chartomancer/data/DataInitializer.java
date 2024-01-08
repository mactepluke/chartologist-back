package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.analytics.ComputationSettings;
import co.syngleton.chartomancer.analytics.ComputationType;
import co.syngleton.chartomancer.analytics.PatternSettings;
import co.syngleton.chartomancer.domain.CoreData;

import java.util.List;

interface DataInitializer {
    CoreData initializeCoreData(String dataFolderName,
                                List<String> dataFilesNames,
                                boolean runAnalysisAtStartup,
                                boolean generateTradingData,
                                boolean createGraphsForMissingTimeframes,
                                boolean loadCoreDataAtStartup,
                                boolean overrideSavedCoreData,
                                boolean overrideSavedTestCoreData,
                                boolean createTimestampedCoreDataArchive,
                                PurgeOption purgeAfterTradingDataGeneration,
                                PatternSettings.Autoconfig patternSettingsAutoconfig,
                                ComputationSettings.Autoconfig computationSettings,
                                ComputationType computationType,
                                PatternSettings.PatternType computablePatternType,
                                boolean atomicPartition,
                                boolean fullScope,
                                boolean launchAutomation
    );
}
