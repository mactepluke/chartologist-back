package co.syngleton.chartomancer.analytics.dao;

import co.syngleton.chartomancer.analytics.data.CoreData;

public interface CoreDataDAO {

    CoreData loadCoreDataFrom(String dataSourceName);

    boolean saveCoreDataTo(CoreData coreData, String dataSourceName);

}
