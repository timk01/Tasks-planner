package tasksplanner.dto;

public record EmailSendingTask(
        String recipient,
        String subject,
        String text
) {
}
