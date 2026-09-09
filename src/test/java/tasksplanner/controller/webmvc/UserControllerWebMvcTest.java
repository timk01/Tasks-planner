package tasksplanner.controller.webmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tasksplanner.controller.UserController;
import tasksplanner.request.UserLoginRequest;
import tasksplanner.request.UserRegisterRequest;
import tasksplanner.response.UserResponse;
import tasksplanner.security.JwtService;
import tasksplanner.service.UserService;

import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerWebMvcTest {
    private static final String VALID_EMAIL = "tim11@mail.ru";
    private static final String VALID_PASSWORD = "sadfasfkljkjl22##";

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void registrationIsSucceeded() throws Exception {
        String email = "tim11@mail.ru";
        String passwordOriginal = "sadfasfkljkjl22##";
        String passwordConfirmation = "sadfasfkljkjl22##";
        UserRegisterRequest dto = new UserRegisterRequest(email, passwordOriginal, passwordConfirmation);

        UserResponse userResponse = new UserResponse(1L, email);

        when(userService.register(dto)).thenReturn(userResponse);

        Jwt jwt = mock(Jwt.class);
        String generatedToken
                = "eyJraWQiOiJCWjZZSDV1b2RSZnk5UGwyZ3owNndVdGJvNGdqNkFrWUs1Vm1waUpRRW1FIiwiYWxnIjoiSFMyNTYifQ";
        when(jwt.getTokenValue()).thenReturn(generatedToken);

        when(jwtService.generateToken(userResponse)).thenReturn(jwt);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(userResponse.id()))
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(header().string(
                        HttpHeaders.AUTHORIZATION, "Bearer " + generatedToken
                ));
    }

    @Test
    public void loginIsSucceeded() throws Exception {
        String email = "tim11@mail.ru";
        String passwordOriginal = "sadfasfkljkjl22##";
        UserLoginRequest dto = new UserLoginRequest(email, passwordOriginal);

        UserResponse userResponse = new UserResponse(1L, email);

        when(userService.findUserByEmail(email)).thenReturn(userResponse);

        Jwt jwt = mock(Jwt.class);
        String generatedToken
                = "eyJraWQiOiJCWjZZSDV1b2RSZnk5UGwyZ3owNndVdGJvNGdqNkFrWUs1Vm1waUpRRW1FIiwiYWxnIjoiSFMyNTYifQ";
        when(jwt.getTokenValue()).thenReturn(generatedToken);

        when(jwtService.generateToken(userResponse)).thenReturn(jwt);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(userResponse.id()))
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(header().string(
                        HttpHeaders.AUTHORIZATION, "Bearer " + generatedToken
                ));
    }

    @ParameterizedTest
    @MethodSource("invalidRegistrationData")
    public void registrationFailedDueToInvalidData(String email, String password, String repeatedPassword)
            throws Exception {
        UserRegisterRequest dto = new UserRegisterRequest(email, password, repeatedPassword);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    private static Stream<Arguments> invalidRegistrationData() {
        return Stream.of(
                Arguments.of("", VALID_PASSWORD, VALID_PASSWORD),
                Arguments.of(" ", VALID_PASSWORD, VALID_PASSWORD),
                Arguments.of("aaaaa", VALID_PASSWORD, VALID_PASSWORD),
                Arguments.of("a".repeat(255), VALID_PASSWORD, VALID_PASSWORD),
                Arguments.of("tim-1", VALID_PASSWORD, VALID_PASSWORD),

                Arguments.of(VALID_EMAIL, "", VALID_PASSWORD),
                Arguments.of(VALID_EMAIL, " ", VALID_PASSWORD),
                Arguments.of(VALID_EMAIL, "aaaa", VALID_PASSWORD),
                Arguments.of(VALID_EMAIL, "a".repeat(21), VALID_PASSWORD),
                Arguments.of(VALID_EMAIL, "abc 12", VALID_PASSWORD),

                Arguments.of(VALID_EMAIL, VALID_PASSWORD, ""),

                Arguments.of(VALID_EMAIL, VALID_PASSWORD, "anotherValid12##")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidLoginData")
    public void loginFailedDueToInvalidData(String email, String password) throws Exception {
        UserLoginRequest dto = new UserLoginRequest(email, password);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    private static Stream<Arguments> invalidLoginData() {
        return Stream.of(
                Arguments.of("", VALID_PASSWORD),
                Arguments.of(" ", VALID_PASSWORD),
                Arguments.of("aaaaa", VALID_PASSWORD),
                Arguments.of("a".repeat(255), VALID_PASSWORD),
                Arguments.of("tim-1", VALID_PASSWORD),

                Arguments.of(VALID_EMAIL, ""),
                Arguments.of(VALID_EMAIL, " "),
                Arguments.of(VALID_EMAIL, "aaaa"),
                Arguments.of(VALID_EMAIL, "a".repeat(21)),
                Arguments.of(VALID_EMAIL, "abc 12")
        );
    }

    @Test
    public void registrationFailedDueToInvalidData()
            throws Exception {

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void loginFailedDueToInvalidData()
            throws Exception {

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void registrationFailedDueToMalformedJson()
            throws Exception {
        String malformedJson = """
                {
                  "email": "tim11@mail.ru",
                  "password": "abcde"
                  "repeatedPassword": "abcde"
                }
                """;

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void loginFailedDueToMalformedJson()
            throws Exception {
        String malformedJson = """
                {
                  "email": "tim11@mail.ru"
                  "password": "abcde"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}