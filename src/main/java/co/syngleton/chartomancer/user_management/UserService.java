package co.syngleton.chartomancer.user_management;

public interface UserService {

    User create(String username, String password);

    User find(String username);

    User update(User user);

    void delete(String username);

}
