package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.core_entities.CoreData;

interface CoreDataDAO {
    CoreData loadCoreDataFrom(String dataSourceName);

    boolean saveCoreDataTo(CoreData coreData, String dataSourceName);

}
