package tasksplanner.controller.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import tasksplanner.entity.User;
import tasksplanner.exception.managed.EmailAlreadyExistsException;
import tasksplanner.exception.managed.InvalidLoginDataException;
import tasksplanner.repository.UserRepository;
import tasksplanner.request.UserLoginRequest;
import tasksplanner.request.UserRegisterRequest;
import tasksplanner.response.UserResponse;
import tasksplanner.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;


    @Test
    public void registerIsSucceeded() {
        String email = "tim11@mail.ru";
        boolean emailIsFound = false;
        when(repository.existsByEmail(email)).thenReturn(emailIsFound);

        String passwordOriginal = "sadfasfkljkjl22##";
        String passwordHashed = "sadfasfkljkjl22##_mocked_hash";
        when(encoder.encode(passwordOriginal)).thenReturn(passwordHashed);

        User savedUser = new User(email, passwordHashed);
        ReflectionTestUtils.setField(savedUser, "id", 1L);
        when(repository.save(any(User.class))).thenReturn(savedUser);

        String passwordConfirmation = "sadfasfkljkjl22##";
        UserRegisterRequest dto = new UserRegisterRequest(email, passwordOriginal, passwordConfirmation);
        UserResponse expected = new UserResponse(1L, email);
        UserResponse actual = service.register(dto);

        verify(repository, times(1)).existsByEmail(email);
        verify(encoder, times(1)).encode(passwordOriginal);
        verify(repository, times(1)).save(userArgumentCaptor.capture());

        User captorValue = userArgumentCaptor.getValue();

        assertThat(captorValue).isNotNull();
        assertThat(captorValue.getEmail()).isEqualTo(email);
        assertThat(captorValue.getPassword()).isEqualTo(passwordHashed);

        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(expected.id());
        assertThat(actual.email()).isEqualTo(expected.email());
    }

    @Test
    public void findByEmailIsSucceeded() {
        String email = "tim11@mail.ru";
        String passwordHashed = "sadfasfkljkjl22##_mocked_hash";
        User user = new User(email, passwordHashed);
        ReflectionTestUtils.setField(user, "id", 1L);
        Optional<User> savedUser = Optional.of(user);
        when(repository.findByEmail(email)).thenReturn(savedUser);

        UserResponse expected = new UserResponse(1L, email);
        UserResponse actual = service.findUserByEmail(email);

        verify(repository, times(1)).findByEmail(email);
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(expected.id());
        assertThat(actual.email()).isEqualTo(expected.email());
    }

    @Test
    public void registerIsFailedSinceUserExists() {
        String email = "tim11@mail.ru";
        String passwordOriginal = "sadfasfkljkjl22##";

        boolean emailIsFound = true;
        when(repository.existsByEmail(email)).thenReturn(emailIsFound);

        String passwordConfirmation = "sadfasfkljkjl22##";
        UserRegisterRequest dto = new UserRegisterRequest(email, passwordOriginal, passwordConfirmation);

        assertThatThrownBy(() -> service.register(dto))
                .isInstanceOf(EmailAlreadyExistsException.class);
        verify(repository, times(1)).existsByEmail(email);
        verify(encoder, never()).encode(passwordOriginal);
    }

    @Test
    public void findUserByEmailIsFailedSinceEmailDoesNoFound() {
        String email = "tim11@mail.ru";

        when(repository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findUserByEmail(email))
                .isInstanceOf(InvalidLoginDataException.class);
        verify(repository, times(1)).findByEmail(email);
    }
}
