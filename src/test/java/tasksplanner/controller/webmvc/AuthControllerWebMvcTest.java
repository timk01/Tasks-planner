package tasksplanner.controller.webmvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tasksplanner.controller.AuthController;
import tasksplanner.response.UserResponse;
import tasksplanner.service.UserService;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    public void getUserIsSucceeded() throws Exception {
        String email = "tim11@mail.ru";
        UserResponse userResponse = new UserResponse(1L, email);
        when(userService.findUserByEmail(email)).thenReturn(userResponse);

        mockMvc.perform(get("/user")
                        .with(jwt().jwt(jwt -> jwt.claim("email", email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(userResponse.id()))
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    public void getUserFailedDueToNoTokenProvided() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isUnauthorized());
    }
}