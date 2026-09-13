package tasksplanner.controller.integration.task;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import tasksplanner.controller.integration.AbstractIntegrationTest;
import tasksplanner.entity.TaskStatus;
import tasksplanner.request.TaskCreateRequest;
import tasksplanner.response.TaskResponse;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetTasksIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void getTasksIsSucceeded() throws Exception {
        RegisteredUser registeredUser = registerUser();

        String header = "task_number_N";
        String text = "task_number_N_description";
        TaskCreateRequest createDto = new TaskCreateRequest(header, text);

        MvcResult result1 = mockMvc.perform(post("/tasks")
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

        TaskResponse createdTask1 = jsonMapper.readValue(
                result1.getResponse().getContentAsString(),
                TaskResponse.class
        );

        Long firstTTaskId = createdTask1.taskId();

        MvcResult result2 = mockMvc.perform(post("/tasks")
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

        TaskResponse createdTask2 = jsonMapper.readValue(
                result2.getResponse().getContentAsString(),
                TaskResponse.class
        );

        Long secondTaskId = createdTask2.taskId();

        mockMvc.perform(get("/tasks")
                        .header(HttpHeaders.AUTHORIZATION, registeredUser.authorization()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].taskId").exists())
                .andExpect(jsonPath("$[0].taskId").value(firstTTaskId))
                .andExpect(jsonPath("$[0].header").exists())
                .andExpect(jsonPath("$[0].header").value(createdTask1.header()))
                .andExpect(jsonPath("$[0].text").exists())
                .andExpect(jsonPath("$[0].text").value(createdTask1.text()))
                .andExpect(jsonPath("$[0].status").exists())
                .andExpect(jsonPath("$[0].status").value(TaskStatus.CREATED.name()))
                .andExpect(jsonPath("$[0].finishedAt").value(nullValue()))
                .andExpect(jsonPath("$[0].ownerId").exists())
                .andExpect(jsonPath("$[0].ownerId").value(registeredUser.id()))

                .andExpect(jsonPath("$[1].taskId").exists())
                .andExpect(jsonPath("$[1].taskId").value(secondTaskId))
                .andExpect(jsonPath("$[1].header").exists())
                .andExpect(jsonPath("$[1].header").value(createdTask2.header()))
                .andExpect(jsonPath("$[1].text").exists())
                .andExpect(jsonPath("$[1].text").value(createdTask2.text()))
                .andExpect(jsonPath("$[1].status").exists())
                .andExpect(jsonPath("$[1].status").value(TaskStatus.CREATED.name()))
                .andExpect(jsonPath("$[1].finishedAt").value(nullValue()))
                .andExpect(jsonPath("$[1].ownerId").exists())
                .andExpect(jsonPath("$[1].ownerId").value(registeredUser.id()));
    }
}