package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.domain.CoreData;
import co.syngleton.chartomancer.pattern_recognition.ComputationSettings;
import co.syngleton.chartomancer.pattern_recognition.ComputationType;
import co.syngleton.chartomancer.pattern_recognition.PatternSettings;

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
