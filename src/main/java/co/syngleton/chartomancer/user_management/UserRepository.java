package co.syngleton.chartomancer.user_management;

interface UserRepository {

    User create(User user);

    User read(String username);

    User update(User user);

    void delete(String username);
}
