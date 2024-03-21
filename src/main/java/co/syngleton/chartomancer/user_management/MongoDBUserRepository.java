package co.syngleton.chartomancer.user_management;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface MongoDBUserRepository extends MongoRepository<User, String> {

    User findByUsername(String username);

    void deleteByUsername(String username);
}
