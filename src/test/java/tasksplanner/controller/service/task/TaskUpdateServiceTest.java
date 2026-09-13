package tasksplanner.controller.service.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tasksplanner.entity.Task;
import tasksplanner.entity.TaskStatus;
import tasksplanner.entity.User;
import tasksplanner.exception.managed.IllegalTaskStatusChangeException;
import tasksplanner.exception.managed.InvalidLoginDataException;
import tasksplanner.exception.managed.TaskIsNotFoundException;
import tasksplanner.mapper.TaskMapper;
import tasksplanner.repository.TaskRepository;
import tasksplanner.repository.UserRepository;
import tasksplanner.request.TaskUpdateRequest;
import tasksplanner.response.TaskResponse;
import tasksplanner.service.TaskService;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskUpdateServiceTest {

    @InjectMocks
    private TaskService service;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper mapper;
    @Captor
    private ArgumentCaptor<Task> taskArgumentCaptor;

    @Test
    public void updateTaskIsSucceeded() {
        long taskId = 1L;
        long userId = 1L;

        User user = new User("smth@mail.ru", "password");
        ReflectionTestUtils.setField(user, "id", 1L);
        String header = "task_number_N";
        String text = "task_number_N_description";
        Optional<Task> task = Optional.of(new Task(
                header,
                text,
                TaskStatus.CREATED,
                null,
                user
        ));
        when(taskRepository.findByIdAndTaskOwner_Id(taskId, userId)).thenReturn(task);

        String headerUpdated = "task_number_N_updated";
        String textUpdated = "task_number_N_description_updated";
        TaskStatus statusUpdated = TaskStatus.IN_PROCESS;

        Task taskUpdated = new Task(
                headerUpdated,
                textUpdated,
                statusUpdated,
                null,
                user
        );
        ReflectionTestUtils.setField(taskUpdated, "id", 1L);
        when(taskRepository.save(any(Task.class))).thenReturn(taskUpdated);

        TaskResponse expected = new TaskResponse(
                1L,
                headerUpdated,
                textUpdated,
                statusUpdated,
                null,
                1L
        );
        when(mapper.toTaskResponse(taskUpdated)).thenReturn(expected);

        TaskUpdateRequest dto = new TaskUpdateRequest(headerUpdated, textUpdated, statusUpdated);
        TaskResponse actual = service.updateTask(taskId, userId, dto);

        verify(taskRepository, times(1)).findByIdAndTaskOwner_Id(taskId, userId);
        verify(taskRepository, times(1)).save(taskArgumentCaptor.capture());
        verify(mapper, times(1)).toTaskResponse(taskUpdated);

        Task captorValue = taskArgumentCaptor.getValue();
        assertThat(captorValue).isNotNull();

        assertThat(captorValue.getHeader()).isEqualTo(headerUpdated);
        assertThat(captorValue.getText()).isEqualTo(textUpdated);
        assertThat(captorValue.getTaskStatus()).isEqualTo(statusUpdated);
        assertThat(captorValue.getFinishedAt()).isNull();
        assertThat(captorValue.getTaskOwner()).isEqualTo(user);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("nothingToUpdateTaskData")
    public void updateTaskIsSucceededButNothingToChange(
            String updatedHeader,
            String updatedText,
            TaskStatus initialStatus,
            TaskStatus updatedStatus) {
        long taskId = 1L;
        long userId = 1L;

        User user = new User("smth@mail.ru", "password");
        ReflectionTestUtils.setField(user, "id", 1L);
        String header = "task_number_N";
        String text = "task_number_N_description";
        Optional<Task> task = Optional.of(new Task(
                header,
                text,
                initialStatus,
                null,
                user
        ));
        when(taskRepository.findByIdAndTaskOwner_Id(taskId, userId)).thenReturn(task);

        TaskResponse expected = new TaskResponse(
                1L,
                header,
                text,
                initialStatus,
                null,
                1L
        );
        when(mapper.toTaskResponse(task.get())).thenReturn(expected);

        TaskUpdateRequest dto = new TaskUpdateRequest(updatedHeader, updatedText, updatedStatus);
        TaskResponse actual = service.updateTask(taskId, userId, dto);

        verify(taskRepository, times(1)).findByIdAndTaskOwner_Id(taskId, userId);
        verify(taskRepository, never()).save(any());
        verify(mapper, times(1)).toTaskResponse(task.get());

        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> nothingToUpdateTaskData() {
        return Stream.of(
                Arguments.of(null, null, TaskStatus.CREATED, null),
                Arguments.of(null, null, TaskStatus.CREATED, TaskStatus.CREATED),
                Arguments.of(null, null, TaskStatus.IN_PROCESS, TaskStatus.IN_PROCESS),
                Arguments.of(null, null, TaskStatus.FINISHED, TaskStatus.FINISHED)
        );
    }

    @Test
    public void updateTaskIsSucceededHeaderIsNull() {
        long taskId = 1L;
        long userId = 1L;

        User user = new User("smth@mail.ru", "password");
        ReflectionTestUtils.setField(user, "id", 1L);
        String header = "task_number_N";
        String text = "task_number_N_description";
        Optional<Task> task = Optional.of(new Task(
                header,
                text,
                TaskStatus.CREATED,
                null,
                user
        ));
        when(taskRepository.findByIdAndTaskOwner_Id(taskId, userId)).thenReturn(task);

        String textUpdated = "task_number_N_description_updated";
        TaskStatus statusUpdated = TaskStatus.IN_PROCESS;

        Task taskUpdated = new Task(
                header,
                textUpdated,
                statusUpdated,
                null,
                user
        );
        ReflectionTestUtils.setField(taskUpdated, "id", 1L);
        when(taskRepository.save(any(Task.class))).thenReturn(taskUpdated);

        TaskResponse expected = new TaskResponse(
                1L,
                header,
                textUpdated,
                statusUpdated,
                null,
                1L
        );
        when(mapper.toTaskResponse(taskUpdated)).thenReturn(expected);

        TaskUpdateRequest dto = new TaskUpdateRequest(null, textUpdated, statusUpdated);
        TaskResponse actual = service.updateTask(taskId, userId, dto);

        verify(taskRepository, times(1)).findByIdAndTaskOwner_Id(taskId, userId);
        verify(taskRepository, times(1)).save(taskArgumentCaptor.capture());
        verify(mapper, times(1)).toTaskResponse(taskUpdated);

        Task captorValue = taskArgumentCaptor.getValue();
        assertThat(captorValue).isNotNull();

        assertThat(captorValue.getHeader()).isEqualTo(header);
        assertThat(captorValue.getText()).isEqualTo(textUpdated);
        assertThat(captorValue.getTaskStatus()).isEqualTo(statusUpdated);
        assertThat(captorValue.getFinishedAt()).isNull();
        assertThat(captorValue.getTaskOwner()).isEqualTo(user);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void updateTaskIsSucceededTextIsNull() {
        long taskId = 1L;
        long userId = 1L;

        User user = new User("smth@mail.ru", "password");
        ReflectionTestUtils.setField(user, "id", 1L);
        String header = "task_number_N";
        String text = "task_number_N_description";
        Optional<Task> task = Optional.of(new Task(
                header,
                text,
                TaskStatus.CREATED,
                null,
                user
        ));
        when(taskRepository.findByIdAndTaskOwner_Id(taskId, userId)).thenReturn(task);

        String headerUpdated = "task_number_N_updated";
        TaskStatus statusUpdated = TaskStatus.IN_PROCESS;

        Task taskUpdated = new Task(
                headerUpdated,
                text,
                statusUpdated,
                null,
                user
        );
        ReflectionTestUtils.setField(taskUpdated, "id", 1L);
        when(taskRepository.save(any(Task.class))).thenReturn(taskUpdated);

        TaskResponse expected = new TaskResponse(
                1L,
                headerUpdated,
                text,
                statusUpdated,
                null,
                1L
        );
        when(mapper.toTaskResponse(taskUpdated)).thenReturn(expected);

        TaskUpdateRequest dto = new TaskUpdateRequest(headerUpdated, null, statusUpdated);
        TaskResponse actual = service.updateTask(taskId, userId, dto);

        verify(taskRepository, times(1)).findByIdAndTaskOwner_Id(taskId, userId);
        verify(taskRepository, times(1)).save(taskArgumentCaptor.capture());
        verify(mapper, times(1)).toTaskResponse(taskUpdated);

        Task captorValue = taskArgumentCaptor.getValue();
        assertThat(captorValue).isNotNull();

        assertThat(captorValue.getHeader()).isEqualTo(headerUpdated);
        assertThat(captorValue.getText()).isEqualTo(text);
        assertThat(captorValue.getTaskStatus()).isEqualTo(statusUpdated);
        assertThat(captorValue.getFinishedAt()).isNull();
        assertThat(captorValue.getTaskOwner()).isEqualTo(user);

        assertThat(actual).isEqualTo(expected);
    }


    @ParameterizedTest
    @MethodSource("statusUpdateOnlyData")
    public void updateTaskIsSucceededWithStatusChangeOnly(
            String updatedHeader,
            String updatedText,
            TaskStatus initialStatus,
            TaskStatus updatedStatus) {
        long taskId = 1L;
        long userId = 1L;

        User user = new User("smth@mail.ru", "password");
        ReflectionTestUtils.setField(user, "id", 1L);
        String header = "task_number_N";
        String text = "task_number_N_description";
        Optional<Task> task = Optional.of(new Task(
                header,
                text,
                initialStatus,
                null,
                user
        ));
        when(taskRepository.findByIdAndTaskOwner_Id(taskId, userId)).thenReturn(task);

        OffsetDateTime finishedAt = OffsetDateTime.now();

        Task taskUpdated = new Task(
                header,
                text,
                updatedStatus,
                finishedAt,
                user
        );

        ReflectionTestUtils.setField(taskUpdated, "id", 1L);
        when(taskRepository.save(any(Task.class))).thenReturn(taskUpdated);

        TaskResponse expected = new TaskResponse(
                1L,
                header,
                text,
                updatedStatus,
                finishedAt,
                1L
        );

        when(mapper.toTaskResponse(taskUpdated)).thenReturn(expected);

        TaskUpdateRequest dto = new TaskUpdateRequest(updatedHeader, updatedText, updatedStatus);
        TaskResponse actual = service.updateTask(taskId, userId, dto);

        verify(taskRepository, times(1)).findByIdAndTaskOwner_Id(taskId, userId);
        verify(taskRepository, times(1)).save(taskArgumentCaptor.capture());
        verify(mapper, times(1)).toTaskResponse(taskUpdated);

        Task captorValue = taskArgumentCaptor.getValue();
        assertThat(captorValue).isNotNull();

        assertThat(captorValue.getHeader()).isEqualTo(header);
        assertThat(captorValue.getText()).isEqualTo(text);
        assertThat(captorValue.getTaskStatus()).isEqualTo(updatedStatus);
        assertThat(captorValue.getFinishedAt()).isNotNull();
        assertThat(captorValue.getTaskOwner()).isEqualTo(user);

        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> statusUpdateOnlyData() {
        return Stream.of(
                Arguments.of(null, null, TaskStatus.CREATED, TaskStatus.FINISHED),
                Arguments.of(null, null, TaskStatus.IN_PROCESS, TaskStatus.FINISHED)
        );
    }

    @ParameterizedTest
    @MethodSource("statusUpdateWronglyData")
    public void updateTaskFailedDueToWrongNewStatus(
            String updatedHeader,
            String updatedText,
            TaskStatus initialStatus,
            TaskStatus updatedStatus
    ) {
        long taskId = 1L;
        long userId = 1L;

        User user = new User("smth@mail.ru", "password");
        ReflectionTestUtils.setField(user, "id", 1L);
        String header = "task_number_N";
        String text = "task_number_N_description";
        Optional<Task> task = Optional.of(new Task(
                header,
                text,
                initialStatus,
                null,
                user
        ));
        when(taskRepository.findByIdAndTaskOwner_Id(taskId, userId)).thenReturn(task);

        TaskUpdateRequest dto = new TaskUpdateRequest(updatedHeader, updatedText, updatedStatus);
        assertThatThrownBy(() -> service.updateTask(taskId, userId, dto))
                .isInstanceOf(IllegalTaskStatusChangeException.class);

        verify(taskRepository, times(1)).findByIdAndTaskOwner_Id(taskId, userId);
        verify(taskRepository, never()).save(any());
        verify(mapper, never()).toTaskResponse(any());
    }

    private static Stream<Arguments> statusUpdateWronglyData() {
        return Stream.of(
                Arguments.of(null, null, TaskStatus.IN_PROCESS, TaskStatus.CREATED),
                Arguments.of(null, null, TaskStatus.FINISHED, TaskStatus.CREATED),
                Arguments.of(null, null, TaskStatus.FINISHED, TaskStatus.IN_PROCESS)
        );
    }

    @Test
    public void updateTaskFailedDueToNotFoundTask() {
        long taskId = 1L;
        long userId = 1L;

        when(taskRepository.findByIdAndTaskOwner_Id(taskId, userId))
                .thenReturn(Optional.empty());
        TaskUpdateRequest dto = new TaskUpdateRequest("smthss", "updatedText", TaskStatus.CREATED);
        assertThatThrownBy(() -> service.updateTask(taskId, userId, dto))
                .isInstanceOf(TaskIsNotFoundException.class);

        verify(taskRepository, times(1)).findByIdAndTaskOwner_Id(taskId, userId);
        verify(taskRepository, never()).save(any());
        verify(mapper, never()).toTaskResponse(any());
    }
}
