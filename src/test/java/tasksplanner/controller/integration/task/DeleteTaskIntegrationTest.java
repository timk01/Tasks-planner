package tasksplanner.controller.integration.task;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import tasksplanner.controller.integration.AbstractIntegrationTest;
import tasksplanner.entity.TaskStatus;
import tasksplanner.request.TaskCreateRequest;
import tasksplanner.response.TaskResponse;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DeleteTaskIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void deleteTaskIsSucceeded() throws Exception {
        RegisteredUser registeredUser = registerUser();

        String header = "task_number_N";
        String text = "task_number_N_description";
        TaskCreateRequest createDto = new TaskCreateRequest(header, text);

        MvcResult result = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(createDto))
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
                .andExpect(jsonPath("$.ownerId").value(registeredUser.id()))
                .andReturn();

        TaskResponse createdTask = jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                TaskResponse.class
        );

        Long taskId = createdTask.taskId();

        mockMvc.perform(delete("/tasks/" + taskId)
                        .header(HttpHeaders.AUTHORIZATION, registeredUser.authorization()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteTaskFailedDueToNotFoundTask() throws Exception {
        RegisteredUser registeredUser = registerUser();

        String header = "task_number_N";
        String text = "task_number_N_description";
        TaskCreateRequest createDto = new TaskCreateRequest(header, text);

        MvcResult result = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(createDto))
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
                .andExpect(jsonPath("$.ownerId").value(registeredUser.id()))
                .andReturn();

        TaskResponse createdTask = jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                TaskResponse.class
        );

        Long taskId = createdTask.taskId() + 1;

        mockMvc.perform(delete("/tasks/" + taskId)
                        .header(HttpHeaders.AUTHORIZATION, registeredUser.authorization()))
                .andExpect(status().isNotFound());
    }
}