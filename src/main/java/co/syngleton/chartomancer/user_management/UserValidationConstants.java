package co.syngleton.chartomancer.user_management;

public class UserValidationConstants {

    private UserValidationConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String USERNAME_PATTERN = "[A-Za-zÀ-ÿ-]{3,50}$";
    public static final String PASSWORD_PATTERN = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&]).{8,30}$";
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{6,50}$";
    public static final String USERNAME_MESSAGE = "Username must be 3-50 chars.";
    public static final String PASSWORD_MESSAGE = "Password must be 8-30 chars, at least 1 lower case, 1 upper case and 1 special char.";
    public static final String EMAIL_MESSAGE = "Email must be of correct format, and between 6-50 chars.";
}
