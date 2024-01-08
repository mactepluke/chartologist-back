package co.syngleton.chartomancer.data.dao;

import co.syngleton.chartomancer.data.CoreData;

public interface CoreDataDAO {

    CoreData loadCoreDataFrom(String dataSourceName);

    boolean saveCoreDataTo(CoreData coreData, String dataSourceName);

}
