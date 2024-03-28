package co.syngleton.chartomancer.user_management;

import lombok.extern.log4j.Log4j2;

@Log4j2
class InMemoryUserRepositoryWithDebugLogging extends InMemoryUserRepository {

    private void debugLogDatabaseContent() {
        super.users.forEach((key, value) -> log.debug(value));
    }

    @Override
    public User create(User user) {

        User createdUser = super.create(user);
        debugLogDatabaseContent();

        return createdUser;
    }

    @Override
    public User read(String username) {

        User readUser = super.read(username);
        debugLogDatabaseContent();

        return readUser;
    }

    @Override
    public User update(User user) {

        User updatedUser = super.update(user);
        debugLogDatabaseContent();

        return updatedUser;
    }

    @Override
    public void delete(String username) {
        super.delete(username);
        debugLogDatabaseContent();
    }

}
