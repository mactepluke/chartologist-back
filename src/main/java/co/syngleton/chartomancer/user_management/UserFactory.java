package co.syngleton.chartomancer.user_management;

public interface UserFactory {

    User create(String username, String password, String hiddenPassword);

    User from(UserDTO userDTO);

    UserDTO from(User userDTO);
}
