package tasksplanner.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@PasswordsMatch
public record UserRegisterRequest(
        @NotBlank(message = "Email should not be null and must contain at least one non-whitespace character")
        @Email
        @Size(min = 6, max = 254, message = "Email must be between {min} and {max} characters")
        String email,

        @NotBlank(message = "User password should not be null and must contain at least one non-whitespace character")
        @Size(min = 5, max = 20, message = "User password must be between {min} and {max} characters")
        @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*(),.?\":{}|<>\\[\\]\\\\/`~+=\\-_';]*$", message = "Invalid password")
        String password,

        @NotBlank(message = "User password should not be null and must contain at least one non-whitespace character")
        @Size(min = 5, max = 20, message = "User password must be between {min} and {max} characters")
        @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*(),.?\":{}|<>\\[\\]\\\\/`~+=\\-_';]*$", message = "Invalid password")
        String passwordConfirmation
) {
}