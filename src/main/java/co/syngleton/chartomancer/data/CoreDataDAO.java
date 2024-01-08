package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.domain.CoreData;

interface CoreDataDAO {

    CoreData loadCoreDataFrom(String dataSourceName);

    boolean saveCoreDataTo(CoreData coreData, String dataSourceName);

}
