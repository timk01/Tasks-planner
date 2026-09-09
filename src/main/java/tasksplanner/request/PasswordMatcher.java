package tasksplanner.request;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class PasswordMatcher implements ConstraintValidator<PasswordsMatch, UserRegisterRequest> {

    @Override
    public boolean isValid(UserRegisterRequest value, ConstraintValidatorContext context) {
        return Objects.equals(value.password(), value.passwordConfirmation());
    }
}
