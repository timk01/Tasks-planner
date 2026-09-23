package tasksplanner.controller.service.usertasks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tasksplanner.dto.UserTasks;
import tasksplanner.entity.Task;
import tasksplanner.entity.TaskStatus;
import tasksplanner.entity.User;
import tasksplanner.mapper.TaskMapper;
import tasksplanner.repository.TaskRepository;
import tasksplanner.repository.UserRepository;
import tasksplanner.request.TaskCreateRequest;
import tasksplanner.response.ScheduledTaskResponse;
import tasksplanner.response.TaskResponse;
import tasksplanner.service.TaskService;
import tasksplanner.service.UserTasksService;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetUserTasksServiceTest {

    @InjectMocks
    private UserTasksService service;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper mapper;

    @Test
    public void getUserTasksIsSucceeded() {
        Instant from = Instant.parse("2026-09-23T17:00:00Z");
        Instant to = Instant.parse("2026-09-23T18:00:00Z");

        OffsetDateTime fromOffset = OffsetDateTime.parse("2026-09-23T20:00:00+03:00");
        OffsetDateTime toOffset = OffsetDateTime.parse("2026-09-23T21:00:00+03:00");
        OffsetDateTime finishedAt = OffsetDateTime.parse("2026-09-23T20:30:00+03:00");

        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "email", "user@mail.ru");

        Task finishedTask = new Task();
        finishedTask.setHeader("Finished task");
        finishedTask.setText("Finished task text");
        finishedTask.setTaskStatus(TaskStatus.FINISHED);
        finishedTask.setFinishedAt(finishedAt);
        ReflectionTestUtils.setField(finishedTask, "taskOwner", user);
        List<Task> finishedTasks = List.of(finishedTask);

        Task unfinishedTask = new Task();
        unfinishedTask.setHeader("Unfinished task");
        unfinishedTask.setText("Unfinished task text");
        unfinishedTask.setTaskStatus(TaskStatus.CREATED);
        unfinishedTask.setFinishedAt(null);
        ReflectionTestUtils.setField(unfinishedTask, "taskOwner", user);
        List<Task> unfinishedTasks = List.of(unfinishedTask);

        TaskStatus status = TaskStatus.FINISHED;

        when(taskRepository.findAllFinishedTasksByOwners(status, fromOffset, toOffset))
                .thenReturn(finishedTasks);

        when(taskRepository.findAllUnfinishedTasksByOwners(status))
                .thenReturn(unfinishedTasks);

        ScheduledTaskResponse finishedResponse = new ScheduledTaskResponse(
                10L,
                "Finished task",
                "Finished task text",
                TaskStatus.FINISHED,
                finishedAt,
                1L
        );

        ScheduledTaskResponse unfinishedResponse = new ScheduledTaskResponse(
                11L,
                "Unfinished task",
                "Unfinished task text",
                TaskStatus.CREATED,
                null,
                1L
        );

        ReflectionTestUtils.setField(finishedTask, "id", 10L);
        ReflectionTestUtils.setField(unfinishedTask, "id", 11L);

        when(mapper.toScheduledTaskResponse(finishedTask))
                .thenReturn(finishedResponse);

        when(mapper.toScheduledTaskResponse(unfinishedTask))
                .thenReturn(unfinishedResponse);

        List<UserTasks> expected = List.of(
                new UserTasks(
                        1L,
                        "user@mail.ru",
                        List.of(finishedResponse),
                        List.of(unfinishedResponse)
                )
        );

        List<UserTasks> actual = service.getUserTasks(from, to);

        verify(taskRepository, times(1)).findAllFinishedTasksByOwners(status, fromOffset, toOffset);
        verify(taskRepository, times(1)).findAllUnfinishedTasksByOwners(status);
        verify(mapper, times(1)).toScheduledTaskResponse(finishedTask);
        verify(mapper, times(1)).toScheduledTaskResponse(unfinishedTask);

        assertThat(actual).isEqualTo(expected);
    }
}
