package tasksplanner.exception.managed;

public class EmailAlreadyExistsException extends BaseAppException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}