package tasksplanner.exception.managed;

public class TaskIsNotFoundException extends BaseAppException {
    public TaskIsNotFoundException(String message) {
        super(message);
    }

    public TaskIsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
