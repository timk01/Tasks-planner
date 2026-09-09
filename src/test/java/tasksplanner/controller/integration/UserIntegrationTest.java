package tasksplanner.controller.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;
import tasksplanner.entity.User;
import tasksplanner.request.UserLoginRequest;
import tasksplanner.request.UserRegisterRequest;
import tasksplanner.response.UserResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PasswordEncoder encoder;

    @Test
    public void registerUserIsSucceeded() throws Exception {
        String email = "tim11@mail.ru";
        String passwordOriginal = "sadfasfkljkjl22##";
        String passwordConfirmation = "sadfasfkljkjl22##";
        UserRegisterRequest dto = new UserRegisterRequest(email, passwordOriginal, passwordConfirmation);

        MvcResult result = mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.AUTHORIZATION, org.hamcrest.Matchers.startsWith("Bearer ")
                ))
                .andReturn();

        UserResponse response = jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                UserResponse.class
        );

        User savedUser = userRepository.findByEmail(email).orElseThrow();

        assertThat(response.id()).isEqualTo(savedUser.getId());
        assertThat(response.email()).isEqualTo(savedUser.getEmail());
        assertThat(encoder.matches(
                passwordOriginal,
                savedUser.getPassword()
        )).isTrue();
    }

    @Test
    public void loginAndGetUserIsSucceeded() throws Exception {
        RegisteredUser registeredUser = registerUser();

        User savedUser = userRepository
                .findByEmail(registeredUser.email())
                .orElseThrow();

        UserLoginRequest dto = new UserLoginRequest(
                registeredUser.email(),
                registeredUser.password()
        );

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.email").value(registeredUser.email()))
                .andExpect(header().string(
                        HttpHeaders.AUTHORIZATION,
                        org.hamcrest.Matchers.startsWith("Bearer ")
                ))
                .andReturn();

        String authorization = result.getResponse()
                .getHeader(HttpHeaders.AUTHORIZATION);

        mockMvc.perform(get("/user")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.email").value(registeredUser.email()));
    }
}