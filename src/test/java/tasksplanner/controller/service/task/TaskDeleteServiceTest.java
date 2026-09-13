package tasksplanner.controller.service.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tasksplanner.exception.managed.TaskIsNotFoundException;
import tasksplanner.repository.TaskRepository;
import tasksplanner.service.TaskService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskDeleteServiceTest {

    @InjectMocks
    private TaskService service;

    @Mock
    private TaskRepository taskRepository;

    @Test
    public void deleteTaskIsSucceeded() {
        long taskId = 1L;
        long userId = 1L;
        int returnedValue = 1;

        when(taskRepository.deleteByTaskIdAndOwnerId(taskId, userId)).thenReturn(returnedValue);

        service.deleteTask(taskId, userId);

        verify(taskRepository, times(1)).deleteByTaskIdAndOwnerId(taskId, userId);
    }

    @Test
    public void deleteTaskIsFailed() {
        long taskId = 1L;
        long userId = 1L;
        int returnedValue = 0;

        when(taskRepository.deleteByTaskIdAndOwnerId(taskId, userId)).thenReturn(returnedValue);

        assertThatThrownBy(() -> service.deleteTask(taskId, userId))
                .isInstanceOf(TaskIsNotFoundException.class);

        verify(taskRepository, times(1)).deleteByTaskIdAndOwnerId(taskId, userId);
    }
}
