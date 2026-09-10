package tasksplanner.exception.managed;

public class IllegalTaskStatusChangeException extends BaseAppException {
    public IllegalTaskStatusChangeException(String message) {
        super(message);
    }
}
