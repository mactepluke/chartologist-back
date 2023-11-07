package co.syngleton.chartomancer.analytics.dao;

import co.syngleton.chartomancer.analytics.data.CoreData;

public interface CoreDataDAO {

    CoreData loadCoreDataWithName(String dataSourceName);

    boolean saveCoreDataWithName(CoreData coreData, String dataSourceName);

}
