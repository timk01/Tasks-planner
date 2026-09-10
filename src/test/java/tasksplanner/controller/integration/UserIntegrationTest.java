package tasksplanner.controller.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import tasksplanner.entity.User;
import tasksplanner.request.UserLoginRequest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PasswordEncoder encoder;

    @Test
    public void registerUserIsSucceeded() throws Exception {
        RegisteredUser registeredUser = registerUser();

        User savedUser = userRepository
                .findByEmail(registeredUser.email())
                .orElseThrow();

        assertThat(registeredUser.id()).isEqualTo(savedUser.getId());
        assertThat(registeredUser.email()).isEqualTo(savedUser.getEmail());
        assertThat(encoder.matches(
                registeredUser.password(),
                savedUser.getPassword()
        )).isTrue();

        assertThat(registeredUser.authorization())
                .startsWith("Bearer ");
    }

    @Test
    public void loginIsSucceeded() throws Exception {
        RegisteredUser registeredUser = registerUser();

        User savedUser = userRepository
                .findByEmail(registeredUser.email())
                .orElseThrow();

        UserLoginRequest dto = new UserLoginRequest(
                registeredUser.email(),
                registeredUser.password()
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()))
                .andExpect(header().string(
                        HttpHeaders.AUTHORIZATION,
                        org.hamcrest.Matchers.startsWith("Bearer ")
                ));
    }
}