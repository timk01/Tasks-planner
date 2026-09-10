package tasksplanner.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import tasksplanner.entity.TaskStatus;

public record TaskUpdateRequest(

        @Pattern(regexp = ".*\\S.*",
                message = "Task description must contain at least one non-whitespace character")
        @Size(min = 5, max = 60, message = "Task header must be between {min} and {max} characters")
        String header,


        @Pattern(regexp = ".*\\S.*",
                message = "Task description must contain at least one non-whitespace character")
        String text,
        TaskStatus status
) {
}
