package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.shared_domain.CoreData;

import java.util.List;

public interface DataProcessor {
    boolean loadGraphs(CoreData coreData, String dataFolderName, List<String> dataFilesNames);

    boolean loadCoreData(CoreData coreData);

    boolean loadCoreDataWithName(CoreData coreData, String dataSourceName);

    boolean saveCoreData(CoreData coreData);

    boolean saveCoreDataWithName(CoreData coreData, String dataFileName);

    boolean generateTradingData(CoreData coreData);

    boolean purgeUselessData(CoreData coreData, PurgeOption option);

    boolean createGraphsForMissingTimeframes(CoreData coreData);

    void printCoreData(CoreData coreData);
}
