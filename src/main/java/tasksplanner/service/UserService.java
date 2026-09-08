package tasksplanner.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tasksplanner.entity.User;
import tasksplanner.exception.managed.InvalidLoginDataException;
import tasksplanner.exception.managed.EmailAlreadyExistsException;
import tasksplanner.repository.UserRepository;
import tasksplanner.request.UserLoginRequest;
import tasksplanner.request.UserRegisterRequest;
import tasksplanner.response.UserResponse;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public UserResponse register(UserRegisterRequest userRegisterDto) {
        if (repository.existsByEmail(userRegisterDto.email())) {
            throw new EmailAlreadyExistsException("This email is already taken");
        }

        String encodedPass = encoder.encode(userRegisterDto.password());
        User user = repository.save(new User(userRegisterDto.email(), encodedPass));

        log.info(
                "User is registered: userId={}, email={}",
                user.getId(),
                user.getEmail()
        );

        return new UserResponse(user.getId(), user.getEmail());
    }

    public UserResponse findUserByEmail(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new InvalidLoginDataException("Invalid credentials"));

        log.debug(
                "User is retrieved: userId={}, email={}",
                user.getId(),
                user.getEmail()
        );

        return new UserResponse(user.getId(), user.getEmail());
    }
}
