package tasksplanner.controller.service.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tasksplanner.entity.Task;
import tasksplanner.entity.TaskStatus;
import tasksplanner.entity.User;
import tasksplanner.mapper.TaskMapper;
import tasksplanner.repository.TaskRepository;
import tasksplanner.repository.UserRepository;
import tasksplanner.request.TaskCreateRequest;
import tasksplanner.response.TaskResponse;
import tasksplanner.service.TaskService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskCreateServiceTest {

    @InjectMocks
    private TaskService service;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper mapper;
    @Captor
    private ArgumentCaptor<Task> taskArgumentCaptor;


    @Test
    public void createTaskIsSucceeded() {
        long userId = 1L;

        User user = new User("smth@mail.ru", "password");
        ReflectionTestUtils.setField(user, "id", 1L);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        String header = "task_number_N";
        String text = "task_number_N_description";
        Task task = new Task(
                header,
                text,
                TaskStatus.CREATED,
                null,
                user
        );
        ReflectionTestUtils.setField(task, "id", 1L);

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse expected = new TaskResponse(
                1L,
                header,
                text,
                TaskStatus.CREATED,
                null,
                1L
        );
        when(mapper.toTaskResponse(task)).thenReturn(expected);

        TaskCreateRequest dto = new TaskCreateRequest(header, text);
        TaskResponse actual = service.createTask(userId, dto);

        verify(userRepository, times(1)).getReferenceById(userId);
        verify(taskRepository, times(1)).save(taskArgumentCaptor.capture());
        verify(mapper, times(1)).toTaskResponse(task);

        Task captorValue = taskArgumentCaptor.getValue();
        assertThat(captorValue).isNotNull();

        assertThat(captorValue.getHeader()).isEqualTo(header);
        assertThat(captorValue.getText()).isEqualTo(text);
        assertThat(captorValue.getTaskStatus()).isEqualTo(TaskStatus.CREATED);
        assertThat(captorValue.getFinishedAt()).isNull();
        assertThat(captorValue.getTaskOwner()).isEqualTo(user);

        assertThat(actual).isEqualTo(expected);
    }
}
