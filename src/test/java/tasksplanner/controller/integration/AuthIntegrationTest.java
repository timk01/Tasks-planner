package tasksplanner.controller.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthIntegrationTest extends AbstractIntegrationTest {


    @Test
    public void getCurrentUserIsSucceeded() throws Exception {
        RegisteredUser registeredUser = registerUser();

        mockMvc.perform(get("/user")
                        .header(HttpHeaders.AUTHORIZATION, registeredUser.authorization()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(registeredUser.id()))
                .andExpect(jsonPath("$.email").value(registeredUser.email()));
    }
}