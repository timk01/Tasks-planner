package tasksplanner.controller.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tasksplanner.entity.User;
import tasksplanner.repository.UserRepository;
import tasksplanner.security.UserDetailsServiceForTaskPlanner;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceForTaskPlannerTest {

    @InjectMocks
    private UserDetailsServiceForTaskPlanner service;

    @Mock
    private UserRepository repository;

    @Test
    public void loadUserIsSucceeded() {
        String email = "tim11@mail.ru";
        String passwordOriginal = "sadfasfkljkjl22##";

        Optional<User> user = Optional.of(new User(email, passwordOriginal));

        when(repository.findByEmail(email)).thenReturn(user);

        UserDetails expected = org.springframework.security.core.userdetails.User.builder()
                .username(user.get().getEmail())
                .password(user.get().getPassword())
                .authorities(Collections.emptyList())
                .build();

        UserDetails actual = service.loadUserByUsername(email);

        verify(repository, times(1)).findByEmail(email);

        assertThat(actual.getUsername()).isEqualTo(email);
        assertThat(actual.getPassword()).isEqualTo(passwordOriginal);
        assertThat(actual.getAuthorities()).isEmpty();
    }

    @Test
    public void loadUserFailed() {
        String email = "tim11@mail.ru";

        when(repository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class);
        verify(repository, times(1)).findByEmail(email);
    }
}
