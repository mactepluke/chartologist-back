package co.syngleton.chartomancer.analytics.repository;

import co.syngleton.chartomancer.analytics.data.CoreData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoreDataRepository extends MongoRepository<CoreData, String> {
}
