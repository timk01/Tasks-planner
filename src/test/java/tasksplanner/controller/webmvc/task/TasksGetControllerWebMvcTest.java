package tasksplanner.controller.webmvc.task;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tasksplanner.controller.TaskController;
import tasksplanner.entity.TaskStatus;
import tasksplanner.response.TaskResponse;
import tasksplanner.service.TaskService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TasksGetControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    public void getTasksIsSucceeded() throws Exception {
        String header1 = "task_number_N1";
        String text1 = "task_number_N1_description";
        TaskStatus status1 = TaskStatus.CREATED;
        String header2 = "task_number_N2";
        String text2 = "task_number_N2_description";
        TaskStatus status2 = TaskStatus.IN_PROCESS;

        List<TaskResponse> tasks = List.of(
                new TaskResponse(
                        1L,
                        header1,
                        text1,
                        status1,
                        null,
                        1L
                ),
                new TaskResponse(
                        2L,
                        header2,
                        text2,
                        status2,
                        null,
                        1L
                )
        );

        when(taskService.getUserTasks(1L))
                .thenReturn(tasks);

        mockMvc.perform(get("/tasks")
                        .with(jwt().jwt(jwt -> jwt.subject("1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].taskId").exists())
                .andExpect(jsonPath("$[0].taskId").value(1L))
                .andExpect(jsonPath("$[0].header").exists())
                .andExpect(jsonPath("$[0].header").value(header1))
                .andExpect(jsonPath("$[0].text").exists())
                .andExpect(jsonPath("$[0].text").value(text1))
                .andExpect(jsonPath("$[0].status").exists())
                .andExpect(jsonPath("$[0].status").value(TaskStatus.CREATED.name()))
                .andExpect(jsonPath("$[0].finishedAt").value(nullValue()))
                .andExpect(jsonPath("$[0].ownerId").exists())
                .andExpect(jsonPath("$[0].ownerId").value(1L))

                .andExpect(jsonPath("$[1].taskId").exists())
                .andExpect(jsonPath("$[1].taskId").value(2L))
                .andExpect(jsonPath("$[1].header").exists())
                .andExpect(jsonPath("$[1].header").value(header2))
                .andExpect(jsonPath("$[1].text").exists())
                .andExpect(jsonPath("$[1].text").value(text2))
                .andExpect(jsonPath("$[1].status").exists())
                .andExpect(jsonPath("$[1].status").value(TaskStatus.IN_PROCESS.name()))
                .andExpect(jsonPath("$[1].finishedAt").value(nullValue()))
                .andExpect(jsonPath("$[1].ownerId").exists())
                .andExpect(jsonPath("$[1].ownerId").value(1L));
    }

    /**
     * Other tests for wrong token (malformed/expired) see in integration tests.
     *
     * @throws Exception
     */

    @Test
    public void getTasksFailedDueToNoTokenProvided() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isUnauthorized());
    }
}