package tasksplanner.controller.integration;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommonSpoiledTokenIntegrationTest extends AbstractIntegrationTest {

    @MockitoBean
    private Clock clock;

    @Test
    public void getCurrentUserFailedDueToInvalidToken() throws Exception {
        RegisteredUser registeredUser = registerUserWithExpiredToken();

        mockMvc.perform(get("/user")
                        .header(HttpHeaders.AUTHORIZATION, registeredUser.authorization()))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Task endpoints use the same JWT authentication and authorization configuration.
     * Hence, this test verifies common expired-token behaviour for all CRUD-endpoints for tasks
     */
    @Test
    public void getTasksFailedDueToInvalidToken() throws Exception {
        RegisteredUser registeredUser = registerUserWithExpiredToken();

        mockMvc.perform(get("/tasks")
                        .header(HttpHeaders.AUTHORIZATION, registeredUser.authorization()))
                .andExpect(status().isUnauthorized());
    }

    private RegisteredUser registerUserWithExpiredToken() throws Exception {
        when(clock.instant())
                .thenReturn(Instant.now().minus(Duration.ofHours(4)));

        return registerUser();
    }
}
