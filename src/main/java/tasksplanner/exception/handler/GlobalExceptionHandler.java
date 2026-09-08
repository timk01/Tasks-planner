package tasksplanner.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tasksplanner.exception.managed.BaseAppException;
import tasksplanner.exception.managed.EmailAlreadyExistsException;
import tasksplanner.response.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<Class<? extends BaseAppException>, HttpStatus> KNOWN_EXCEPTIONS_STATUS_MAP
            = new HashMap<>();

    static {
        KNOWN_EXCEPTIONS_STATUS_MAP.put(EmailAlreadyExistsException.class, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BaseAppException.class)
    public ResponseEntity<ErrorResponse> handleKnownExceptions(BaseAppException exception) {
        HttpStatus status = KNOWN_EXCEPTIONS_STATUS_MAP.getOrDefault(
                exception.getClass(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );

        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());

        log.warn(
                "Handled application exception with status: {}, message: {}",
                status,
                exception.getMessage()
        );

        return new ResponseEntity<>(
                errorResponse,
                status
        );
    }

    @ExceptionHandler(
            {
                    BadCredentialsException.class,
                    UsernameNotFoundException.class
            }
    )
    public ResponseEntity<ErrorResponse> handleBadCredentialException(
            Exception exception
    ) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        log.warn(
                "Authentication failed with status: {}, message: {}",
                status,
                exception.getMessage()
        );

        return new ResponseEntity<>(
                new ErrorResponse("Invalid credentials"),
                status
        );
    }

    @ExceptionHandler(
            {
                    MethodArgumentNotValidException.class,
                    HttpMessageNotReadableException.class
            }
    )
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(
            Exception exception
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        log.warn(
                "Invalid request data with status: {}, message: {}",
                status,
                exception.getMessage()
        );

        return new ResponseEntity<>(
                new ErrorResponse("Validation failed"),
                status
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception exception) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        log.error(
                "Unknown exception happened during program work with status: {}; Exception stack:",
                status,
                exception
        );

        return new ResponseEntity<>(
                new ErrorResponse("Unknown exception"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
