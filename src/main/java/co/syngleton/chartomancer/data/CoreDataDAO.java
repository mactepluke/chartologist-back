package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.core_entities.CoreData;

interface CoreDataDAO {
    //TODO Create a record-based serialization or use jsoniter like in the "alerts" project
    CoreData loadCoreDataFrom(String dataSourceName);

    boolean saveCoreDataTo(CoreData coreData, String dataSourceName);

}
