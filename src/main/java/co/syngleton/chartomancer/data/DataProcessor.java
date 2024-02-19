package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.pattern_recognition.PatternSettings;

import java.util.List;

public interface DataProcessor {
    boolean createPatternsForCoreData(CoreData coreData, PatternSettings.Builder settingsInput);

    boolean loadGraphs(CoreData coreData, String dataFolderName, List<String> dataFilesNames);

    boolean loadCoreData(CoreData coreData, String dataSourceName);

    boolean saveCoreData(CoreData coreData, String dataFileName);

    boolean generateTradingData(CoreData coreData);

    boolean createGraphsForMissingTimeframes(CoreData coreData);
}
