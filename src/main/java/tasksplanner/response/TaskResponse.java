package tasksplanner.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import tasksplanner.entity.TaskStatus;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskResponse(
        Long taskId,
        String header,
        String text,
        TaskStatus status,
        OffsetDateTime finishedAt,
        Long ownerId
) {
}
