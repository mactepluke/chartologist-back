package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.CoreDataSnapshot;

interface CoreDataDAO {
    CoreDataSnapshot loadCoreDataFrom(String dataSourceName);

    boolean saveCoreDataTo(CoreData coreData, String dataSourceName);

}
