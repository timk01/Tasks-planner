package tasksplanner.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import tasksplanner.entity.TaskStatus;

import static tasksplanner.request.ValidationConstants.*;

public record TaskUpdateRequest(

        @Pattern(
                regexp = STRING_CONTAINS_NON_WHITESPACE_PATTERN,
                message = "Task header must contain at least one non-whitespace character"
        )
        @Size(
                min = HEADER_MIN_LENGTH,
                max = HEADER_MAX_LENGTH,
                message = "Task header must be between {min} and {max} characters"
        )
        String header,

        @Pattern(
                regexp = STRING_CONTAINS_NON_WHITESPACE_PATTERN,
                message = "Task description must contain at least one non-whitespace character"
        )
        String text,
        TaskStatus status
) {
}
