package tasksplanner.controller.webmvc;

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
import tasksplanner.request.TaskCreateRequest;
import tasksplanner.response.TaskResponse;
import tasksplanner.service.TaskService;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskCreateWebMvcTest {
    private static final String VALID_TEXT = "task_number_N";
    private static final String VALID_HEADER = "task_number_N_description";

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

/*    @MockitoBean
    private*/

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void createTaskIsSucceeded() throws Exception {
        String header = "task_number_N";
        String text = "task_number_N_description";
        TaskCreateRequest dto = new TaskCreateRequest(header, text);

        TaskResponse response = new TaskResponse(
                1L,
                header,
                text,
                TaskStatus.CREATED,
                null,
                1L
        );

        when(taskService.createTask(1L, dto))
                .thenReturn(response);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(jwt().jwt(jwt -> jwt.subject("1"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskId").exists())
                .andExpect(jsonPath("$.taskId").value(1L))
                .andExpect(jsonPath("$.header").exists())
                .andExpect(jsonPath("$.header").value(header))
                .andExpect(jsonPath("$.text").exists())
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(TaskStatus.CREATED.name()))
                .andExpect(jsonPath("$.finishedAt").value(nullValue()))
                .andExpect(jsonPath("$.ownerId").exists())
                .andExpect(jsonPath("$.ownerId").value(1L));
    }

    @ParameterizedTest
    @MethodSource("invalidCreateTaskData")
    public void createTaskFailedDueToInvalidData(String header, String text)
            throws Exception {
        TaskCreateRequest dto = new TaskCreateRequest(header, text);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(jwt().jwt(jwt -> jwt.subject("1"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    private static Stream<Arguments> invalidCreateTaskData() {
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
    public void createTaskFailedDueToInvalidData()
            throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().jwt(jwt -> jwt.subject("1"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void createTaskFailedDueToMalformedJson()
            throws Exception {
        String malformedJson = """
                {
                  "header": "task_number_N"
                  "text": "task_number_N_description"
                }
                """;

        mockMvc.perform(post("/tasks")
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
    public void createTaskFailedDueToNoTokenProvided() throws Exception {
        String okJson = """
                {
                  "header": "task_number_N",
                  "text": "task_number_N_description"
                }
                """;

        mockMvc.perform(post("/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(okJson))
                .andExpect(status().isUnauthorized());
    }
}