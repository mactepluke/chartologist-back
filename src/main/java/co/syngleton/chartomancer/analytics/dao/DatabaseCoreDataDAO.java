package co.syngleton.chartomancer.analytics.dao;

import co.syngleton.chartomancer.analytics.data.CoreData;
import org.springframework.stereotype.Component;

@Component("database")
public class DatabaseCoreDataDAO implements CoreDataDAO {

    @Override
    public CoreData loadCoreDataWithName(String dataSourceName) {
        return null;
    }

    @Override
    public boolean saveCoreDataWithName(CoreData coreData, String dataSourceName) {
        return false;
    }
}
