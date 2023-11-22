package co.syngleton.chartomancer.analytics.repository;

import co.syngleton.chartomancer.analytics.dao.mongo_dto.GraphsMongoDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GraphsDataMongoRepository extends MongoRepository<GraphsMongoDTO, String> {
}
