package tasksplanner.controller.integration.usertasks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import tasksplanner.controller.integration.AbstractIntegrationTest;
import tasksplanner.entity.TaskStatus;
import tasksplanner.request.TaskCreateRequest;
import tasksplanner.request.TaskUpdateRequest;
import tasksplanner.response.TaskResponse;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetUserTasksIntegrationTest extends AbstractIntegrationTest {

    @Value("${scheduler.auth-header}")
    private String schedulerAuthHeader;

    @Value("${scheduler.auth-key}")
    private String schedulerAuthKey;

    @Test
    public void getUserTasksIsSucceeded() throws Exception {
        RegisteredUser registeredUser = registerUser();

        String header = "task_number_N";
        String text = "task_number_N_description";
        TaskCreateRequest createDto = new TaskCreateRequest(header, text);

        MvcResult result = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(createDto))
                        .header(HttpHeaders.AUTHORIZATION, registeredUser.authorization()))
                .andExpect(status().isCreated())
                .andReturn();

        TaskResponse createdTask = jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                TaskResponse.class
        );

        Long firstTaskId = createdTask.taskId();

        header = "task_number_N_updated";
        text = "task_number_N_description_updated";
        TaskStatus status = TaskStatus.FINISHED;
        TaskUpdateRequest updateDto = new TaskUpdateRequest(header, text, status);

        MvcResult updatedResult = mockMvc.perform(patch("/tasks/{taskId}", firstTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(updateDto))
                        .header(HttpHeaders.AUTHORIZATION, registeredUser.authorization()))
                .andExpect(status().isOk())
                .andReturn();


        TaskResponse updatedTask = jsonMapper.readValue(
                updatedResult.getResponse().getContentAsString(),
                TaskResponse.class
        );

        String header2 = "task_number_N_second";
        String text2 = "task_number_N_description_second";
        TaskCreateRequest dto2 = new TaskCreateRequest(header2, text2);
        result = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(dto2))
                        .header(HttpHeaders.AUTHORIZATION, registeredUser.authorization()))
                .andExpect(status().isCreated())
                .andReturn();

        TaskResponse createdTask2 = jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                TaskResponse.class
        );

        Long secondTaskId = createdTask2.taskId();

        OffsetDateTime finishedAt = updatedTask.finishedAt();

        Instant from = finishedAt.toInstant().minus(Duration.ofMinutes(1));
        Instant to = finishedAt.toInstant().plus(Duration.ofMinutes(1));

        mockMvc.perform(get("/tasks/getScheduledTasks")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .header(schedulerAuthHeader, schedulerAuthKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))

                .andExpect(jsonPath("$[0].userId").value(registeredUser.id()))
                .andExpect(jsonPath("$[0].email").value(registeredUser.email()))

                .andExpect(jsonPath("$[0].finishedTasks", hasSize(1)))
                .andExpect(jsonPath("$[0].finishedTasks[0].taskId").value(firstTaskId))
                .andExpect(jsonPath("$[0].finishedTasks[0].header").value(header))
                .andExpect(jsonPath("$[0].finishedTasks[0].text").value(text))
                .andExpect(jsonPath("$[0].finishedTasks[0].status").value(TaskStatus.FINISHED.name()))
                .andExpect(jsonPath("$[0].finishedTasks[0].finishedAt").exists())
                .andExpect(jsonPath("$[0].finishedTasks[0].ownerId").value(registeredUser.id()))

                .andExpect(jsonPath("$[0].unfinishedTasks", hasSize(1)))
                .andExpect(jsonPath("$[0].unfinishedTasks[0].taskId").value(secondTaskId))
                .andExpect(jsonPath("$[0].unfinishedTasks[0].header").value(header2))
                .andExpect(jsonPath("$[0].unfinishedTasks[0].text").value(text2))
                .andExpect(jsonPath("$[0].unfinishedTasks[0].status").value(TaskStatus.CREATED.name()))
                .andExpect(jsonPath("$[0].unfinishedTasks[0].finishedAt").doesNotExist())
                .andExpect(jsonPath("$[0].unfinishedTasks[0].ownerId").value(registeredUser.id()));
    }
}