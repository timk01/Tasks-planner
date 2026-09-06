package tasksplanner.exception.managed;

public class UserNotAuthenticatedException extends BaseAppException {
    public UserNotAuthenticatedException(String message) {
        super(message);
    }
}
