package co.syngleton.chartomancer.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface PatternsDataMongoRepository extends MongoRepository<PatternBoxesMongoDTO, String> {
}
