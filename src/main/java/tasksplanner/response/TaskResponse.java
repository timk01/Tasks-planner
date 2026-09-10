package tasksplanner.response;

import tasksplanner.entity.TaskStatus;

import java.time.OffsetDateTime;

public record TaskResponse(
        Long taskId,
        String header,
        String text,
        TaskStatus status,
        OffsetDateTime finishedAt,
        Long ownerId
) {
}
