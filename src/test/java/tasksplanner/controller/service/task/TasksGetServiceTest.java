package tasksplanner.controller.service.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tasksplanner.entity.Task;
import tasksplanner.entity.TaskStatus;
import tasksplanner.entity.User;
import tasksplanner.mapper.TaskMapper;
import tasksplanner.repository.TaskRepository;
import tasksplanner.response.TaskResponse;
import tasksplanner.service.TaskService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TasksGetServiceTest {

    @InjectMocks
    private TaskService service;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper mapper;


    @Test
    public void getTasksIsSucceeded() {
        long userId = 1L;

        User user = new User("smth@mail.ru", "password");
        ReflectionTestUtils.setField(user, "id", 1L);

        String header1 = "task_number_N1";
        String text1 = "task_number_N1_description";
        TaskStatus status1 = TaskStatus.CREATED;
        String header2 = "task_number_N2";
        String text2 = "task_number_N2_description";
        TaskStatus status2 = TaskStatus.IN_PROCESS;

        List<Task> tasks = List.of(
                new Task(
                        header1,
                        text1,
                        status1,
                        null,
                        user
                ),
                new Task(
                        header2,
                        text2,
                        status2,
                        null,
                        user
                )
        );

        when(taskRepository.findAllByTaskOwner_Id(userId)).thenReturn(tasks);

        List<TaskResponse> tasksResponse = List.of(
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

        when(mapper.toTaskResponseList(tasks)).thenReturn(tasksResponse);

        List<TaskResponse> actual = service.getUserTasks(userId);

        verify(taskRepository, times(1)).findAllByTaskOwner_Id(userId);
        verify(mapper, times(1)).toTaskResponseList(tasks);

        assertThat(actual).containsExactlyElementsOf(tasksResponse);
    }
}
