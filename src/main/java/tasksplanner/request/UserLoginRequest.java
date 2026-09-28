package tasksplanner.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static tasksplanner.request.ValidationConstants.*;

public record UserLoginRequest(
        @NotBlank(message = "Email should not be null and must contain at least one non-whitespace character")
        @Email
        @Size(
                min = EMAIL_MIN_LENGTH,
                max = EMAIL_MAX_LENGTH,
                message = "Email must be between {min} and {max} characters"
        )
        String email,

        @NotBlank(message = "User password should not be null and must contain at least one non-whitespace character")
        @Size(
                min = PASSWORD_MIN_LENGTH,
                max = PASSWORD_MAX_LENGTH,
                message = "User password must be between {min} and {max} characters"
        )
        @Pattern(
                regexp = PASSWORD_PATTERN,
                message = "Invalid password"
        )
        String password
) {
}