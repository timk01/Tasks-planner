package tasksplanner.controller.webmvc.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tasksplanner.controller.TaskController;
import tasksplanner.entity.TaskStatus;
import tasksplanner.request.TaskUpdateRequest;
import tasksplanner.response.TaskResponse;
import tasksplanner.service.TaskService;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskDeleteControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    public void deleteTaskIsSucceeded() throws Exception {
        mockMvc.perform(delete("/tasks/1")
                        .with(jwt().jwt(jwt -> jwt.subject("1"))))
                .andExpect(status().isNoContent());
    }

    /**
     * Other tests for wrong token (malformed/expired) see in integration tests.
     *
     * @throws Exception
     */

    @Test
    public void deleteTaskFailedDueToNoTokenProvided() throws Exception {
        mockMvc.perform(delete("/tasks/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}