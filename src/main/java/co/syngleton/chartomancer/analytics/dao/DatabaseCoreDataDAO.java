package co.syngleton.chartomancer.analytics.dao;

import co.syngleton.chartomancer.analytics.data.CoreData;
import co.syngleton.chartomancer.analytics.repository.CoreDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component("database")
@AllArgsConstructor
public class DatabaseCoreDataDAO implements CoreDataDAO {

    private final CoreDataRepository coreDataRepository;

    @Override
    @Transactional(readOnly = true)
    public CoreData loadCoreDataWithName(String dataSourceName) {
        return coreDataRepository.findById(dataSourceName).orElse(null);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean saveCoreDataWithName(CoreData coreData, String dataSourceName) {
        coreDataRepository.deleteAll();
        coreData.setId(dataSourceName);
        coreDataRepository.insert(coreData);
        return coreDataRepository.findById(dataSourceName).isPresent();
    }
}
