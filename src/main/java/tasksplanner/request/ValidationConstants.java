package tasksplanner.request;

public final class ValidationConstants {

    private ValidationConstants() {
    }

    public static final int EMAIL_MIN_LENGTH = 6;
    public static final int EMAIL_MAX_LENGTH = 254;

    public static final int HEADER_MIN_LENGTH = 5;
    public static final int HEADER_MAX_LENGTH = 60;

    public static final int PASSWORD_MIN_LENGTH = 5;
    public static final int PASSWORD_MAX_LENGTH = 20;
    public static final String PASSWORD_PATTERN
            = "^[a-zA-Z0-9!@#$%^&*(),.?\":{}|<>\\[\\]\\\\/`~+=\\-_';]*$";

    public static final String STRING_CONTAINS_NON_WHITESPACE_PATTERN
            = ".*\\S.*";
}
