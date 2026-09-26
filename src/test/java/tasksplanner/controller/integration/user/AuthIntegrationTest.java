package tasksplanner.controller.integration.user;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import tasksplanner.controller.integration.AbstractIntegrationTest;
import tasksplanner.controller.integration.AbstractIntegrationTestWithMockedKafka;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthIntegrationTest extends AbstractIntegrationTestWithMockedKafka {

    @Test
    public void getCurrentUserIsSucceeded() throws Exception {
        RegisteredUser registeredUser = registerUser();

        mockMvc.perform(get("/user")
                        .header(HttpHeaders.AUTHORIZATION, registeredUser.authorization()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(registeredUser.id()))
                .andExpect(jsonPath("$.email").value(registeredUser.email()));
    }

    @Test
    public void getCurrentUserIsFailedDueToMalformedToken() throws Exception {
        RegisteredUser registeredUser = registerUser();

        mockMvc.perform(get("/user")
                        .header(HttpHeaders.AUTHORIZATION, registeredUser.authorization() + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }
}