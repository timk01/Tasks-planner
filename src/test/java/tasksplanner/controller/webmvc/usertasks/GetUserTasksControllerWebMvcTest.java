package tasksplanner.controller.webmvc.usertasks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tasksplanner.controller.TaskController;
import tasksplanner.controller.UserTasksController;
import tasksplanner.dto.UserTasks;
import tasksplanner.entity.TaskStatus;
import tasksplanner.request.UserRegisterRequest;
import tasksplanner.response.ScheduledTaskResponse;
import tasksplanner.response.TaskResponse;
import tasksplanner.service.TaskService;
import tasksplanner.service.UserTasksService;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserTasksController.class)
@WithMockUser(authorities = "SCHEDULER_SERVICE")
class GetUserTasksControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private UserTasksService taskService;

    @Test
    public void getTasksIsSucceeded() throws Exception {
        List<UserTasks> userTasks = List.of(
                new UserTasks(
                        1L,
                        "user@mail.ru",
                        List.of(
                                new ScheduledTaskResponse(
                                        10L,
                                        "Finished task",
                                        "Finished task text",
                                        TaskStatus.FINISHED,
                                        OffsetDateTime.parse("2026-09-23T17:30:00+03:00"),
                                        1L
                                )
                        ),
                        List.of(
                                new ScheduledTaskResponse(
                                        11L,
                                        "Unfinished task",
                                        "Unfinished task text",
                                        TaskStatus.CREATED,
                                        null,
                                        1L
                                )
                        )
                )
        );

        Instant from = Instant.parse("2026-09-23T17:00:00Z");
        Instant to = Instant.parse("2026-09-23T18:00:00Z");

        when(taskService.getUserTasks(from, to))
                .thenReturn(userTasks);

        mockMvc.perform(get("/tasks/getScheduledTasks")
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))

                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].email").value("user@mail.ru"))

                .andExpect(jsonPath("$[0].finishedTasks", hasSize(1)))
                .andExpect(jsonPath("$[0].finishedTasks[0].taskId").value(10L))
                .andExpect(jsonPath("$[0].finishedTasks[0].header").value("Finished task"))
                .andExpect(jsonPath("$[0].finishedTasks[0].text").value("Finished task text"))
                .andExpect(jsonPath("$[0].finishedTasks[0].status").value(TaskStatus.FINISHED.name()))
                .andExpect(jsonPath("$[0].finishedTasks[0].finishedAt").value("2026-09-23T17:30:00+03:00"))
                .andExpect(jsonPath("$[0].finishedTasks[0].ownerId").value(1L))

                .andExpect(jsonPath("$[0].unfinishedTasks", hasSize(1)))
                .andExpect(jsonPath("$[0].unfinishedTasks[0].taskId").value(11L))
                .andExpect(jsonPath("$[0].unfinishedTasks[0].header").value("Unfinished task"))
                .andExpect(jsonPath("$[0].unfinishedTasks[0].text").value("Unfinished task text"))
                .andExpect(jsonPath("$[0].unfinishedTasks[0].status").value(TaskStatus.CREATED.name()))
                .andExpect(jsonPath("$[0].unfinishedTasks[0].finishedAt").doesNotExist())
                .andExpect(jsonPath("$[0].unfinishedTasks[0].ownerId").value(1L));
    }

    @ParameterizedTest
    @MethodSource("invalidGettingTasksData")
    public void getUserTasksFailedDueToInvalidData(String from, String to)
            throws Exception {
        MockHttpServletRequestBuilder request = get("/tasks/getScheduledTasks");

        if (from != null) {
            request.param("from", from);
        }

        if (to != null) {
            request.param("to", to);
        }


        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> invalidGettingTasksData() {
        return Stream.of(
                Arguments.of(null, "2026-09-23T17:00:00Z"),
                Arguments.of("2026-09-23T17:00:00Z", null),
                Arguments.of("invalid_date", "2026-09-23T17:00:00Z"),
                Arguments.of("2026-09-23T17:00:00Z", "invalid_date")
        );
    }

    @Test
    public void getTasksFailedSinceFromIsBeforeTo() throws Exception {
        Instant from = Instant.parse("2026-09-23T18:00:00Z");
        Instant to = Instant.parse("2026-09-23T17:00:00Z");

        mockMvc.perform(get("/tasks/getScheduledTasks")
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isBadRequest());
    }
}



