package co.syngleton.chartomancer.data;

interface CoreDataDAO {

    CoreData loadCoreDataFrom(String dataSourceName);

    boolean saveCoreDataTo(CoreData coreData, String dataSourceName);

}
