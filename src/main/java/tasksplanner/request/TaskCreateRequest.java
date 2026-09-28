package tasksplanner.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static tasksplanner.request.ValidationConstants.HEADER_MAX_LENGTH;
import static tasksplanner.request.ValidationConstants.HEADER_MIN_LENGTH;

public record TaskCreateRequest(
        @NotBlank(message = "Task header should not be null and must contain at least one non-whitespace character")
        @Size(
                min = HEADER_MIN_LENGTH,
                max = HEADER_MAX_LENGTH,
                message = "Task header must be between {min} and {max} characters"
        )
        String header,

        @NotBlank(message = "Task description should not be null and must contain at least one non-whitespace character")
        String text
) {
}
