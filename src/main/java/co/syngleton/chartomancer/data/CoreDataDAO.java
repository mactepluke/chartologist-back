package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.shared_domain.CoreData;

interface CoreDataDAO {
    //TODO Create a record-based serialization
    CoreData loadCoreDataFrom(String dataSourceName);

    boolean saveCoreDataTo(CoreData coreData, String dataSourceName);

}
