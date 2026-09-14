package tasksplanner.controller.integration.task;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import tasksplanner.controller.integration.AbstractIntegrationTest;
import tasksplanner.entity.TaskStatus;
import tasksplanner.request.TaskCreateRequest;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CreateTaskIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void createTaskIsSucceeded() throws Exception {
        RegisteredUser registeredUser = registerUser();

        String header = "task_number_N";
        String text = "task_number_N_description";
        TaskCreateRequest dto = new TaskCreateRequest(header, text);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, registeredUser.authorization()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskId").exists())
                .andExpect(jsonPath("$.header").exists())
                .andExpect(jsonPath("$.header").value(header))
                .andExpect(jsonPath("$.text").exists())
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(TaskStatus.CREATED.name()))
                .andExpect(jsonPath("$.finishedAt").value(nullValue()))
                .andExpect(jsonPath("$.ownerId").exists())
                .andExpect(jsonPath("$.ownerId").value(registeredUser.id()));
    }
}