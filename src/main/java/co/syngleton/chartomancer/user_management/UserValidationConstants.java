package co.syngleton.chartomancer.user_management;

public class UserValidationConstants {

    private UserValidationConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String USERNAME_PATTERN = "^([a-zA-Z0-9_\\-\\.]+)@(([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,30}$";
    public static final String EMAIL_PATTERN = "^([a-zA-Z0-9_\\-\\.]+)@(([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    public static final String USERNAME_MESSAGE = "Email must be of correct format, and between 6-50 chars.";
    public static final String PASSWORD_MESSAGE = "Password must be 8-30 chars, at least 1 lower case, 1 upper case and 1 numerical.";
    public static final String EMAIL_MESSAGE = "Email must be of correct format, and between 6-50 chars.";
}
