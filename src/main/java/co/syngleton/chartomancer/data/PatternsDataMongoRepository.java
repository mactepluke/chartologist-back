package co.syngleton.chartomancer.analytics.repository;

import co.syngleton.chartomancer.data.mongo_dto.PatternBoxesMongoDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatternsDataMongoRepository extends MongoRepository<PatternBoxesMongoDTO, String> {
}
