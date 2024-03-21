package co.syngleton.chartomancer.user_management;

import org.springframework.stereotype.Component;

@Component
class MongoDBUserRepositoryAdapter implements UserRepository {
    private final MongoDBUserRepository repository;

    MongoDBUserRepositoryAdapter(MongoDBUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User create(User user) {
        return repository.insert(user);
    }

    @Override
    public User read(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public User update(User user) {
        return repository.save(user);
    }

    @Override
    public void delete(String username) {
        repository.deleteByUsername(username);
    }
}
