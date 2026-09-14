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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskUpdateControllerWebMvcTest {
    private static final String VALID_HEADER = "task_number_N";
    private static final String VALID_TEXT = "task_number_N_description";

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void updateTaskIsSucceeded() throws Exception {
        long userId = 1L;
        long taskId = 1L;
        String header = "task_number_N_updated";
        String text = "task_number_N_description_updated";
        TaskStatus status = TaskStatus.IN_PROCESS;
        TaskUpdateRequest dto = new TaskUpdateRequest(header, text, status);

        TaskResponse response = new TaskResponse(
                taskId,
                header,
                text,
                status,
                null,
                userId
        );

        when(taskService.updateTask(taskId, userId, dto))
                .thenReturn(response);

        mockMvc.perform(patch("/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(jwt().jwt(jwt -> jwt.subject("1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").exists())
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.header").exists())
                .andExpect(jsonPath("$.header").value(header))
                .andExpect(jsonPath("$.text").exists())
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(TaskStatus.IN_PROCESS.name()))
                .andExpect(jsonPath("$.finishedAt").value(nullValue()))
                .andExpect(jsonPath("$.ownerId").exists())
                .andExpect(jsonPath("$.ownerId").value(userId));
    }

    @ParameterizedTest
    @MethodSource("invalidUpdateTaskData")
    public void updateTaskFailedDueToInvalidData(String header, String text)
            throws Exception {
        TaskUpdateRequest dto = new TaskUpdateRequest(header, text, TaskStatus.IN_PROCESS);

        mockMvc.perform(patch("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(jwt().jwt(jwt -> jwt.subject("1"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    private static Stream<Arguments> invalidUpdateTaskData() {
        return Stream.of(
                Arguments.of("", VALID_TEXT),
                Arguments.of(" ", VALID_TEXT),
                Arguments.of("aaaa", VALID_TEXT),
                Arguments.of("a".repeat(61), VALID_TEXT),

                Arguments.of(VALID_HEADER, ""),
                Arguments.of(VALID_HEADER, " ")
        );
    }

    @Test
    public void updateTaskFailedDueToInvalidStatus()
            throws Exception {
        String badStatusJson = """
                {
                  "header": "task_number_N",
                  "text": "task_number_N_description",
                  "status": "BANANA"
                }
                """;

        mockMvc.perform(patch("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badStatusJson)
                        .with(jwt().jwt(jwt -> jwt.subject("1"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void updateTaskFailedDueToMissingBody()
            throws Exception {
        mockMvc.perform(patch("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().jwt(jwt -> jwt.subject("1"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void updateTaskFailedDueToMalformedJson()
            throws Exception {
        String malformedJson = """
                {
                  "header": "task_number_N"
                  "text": "task_number_N_description",
                  "status": "CREATED"
                }
                """;

        mockMvc.perform(patch("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson)
                        .with(jwt().jwt(jwt -> jwt.subject("1"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }


    /**
     * Other tests for wrong token (malformed/expired) see in integration tests.
     *
     * @throws Exception
     */

    @Test
    public void updateTaskFailedDueToNoTokenProvided() throws Exception {
        mockMvc.perform(patch("/tasks/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}