package tasksplanner.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank(message = "Task header should not be null and must contain at least one non-whitespace character")
        @Size(min = 5, max = 60, message = "Task header must be between {min} and {max} characters")
        String header,

        @NotBlank(message = "Task description should not be null and must contain at least one non-whitespace character")
        String text
) {
}
