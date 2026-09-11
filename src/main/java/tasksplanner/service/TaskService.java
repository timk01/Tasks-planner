package tasksplanner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tasksplanner.entity.Task;
import tasksplanner.entity.TaskStatus;
import tasksplanner.entity.User;
import tasksplanner.exception.managed.IllegalTaskStatusChangeException;
import tasksplanner.exception.managed.TaskIsNotFoundException;
import tasksplanner.mapper.TaskMapper;
import tasksplanner.repository.TaskRepository;
import tasksplanner.repository.UserRepository;
import tasksplanner.request.TaskRequest;
import tasksplanner.request.TaskUpdateRequest;
import tasksplanner.response.TaskResponse;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper mapper;

    @Transactional
    public TaskResponse createTask(Long userId, TaskRequest taskRequest) {
        User referenceById = userRepository.getReferenceById(userId);

        Task savedTask = taskRepository.save(
                new Task(
                        taskRequest.header(),
                        taskRequest.text(),
                        TaskStatus.CREATED,
                        null,
                        referenceById
                )
        );

        return mapper.toTaskResponse(savedTask);
    }

    /**
     * CREATED -> IN_PROCESS -> FINISHED. --- one way road
     * 1. FINISHED status is a final one.
     * 2. CREATED can become either FINISHED orIN_PROCESS
     * 3. IN_PROCESS cannot become CREATED again.
     * 4. Tasks that don't change their status per se
     * (CREATED - CREATED, IN_PROCESS - IN_PROCESS, FINISHED - FINISHED),
     * remain in same status.
     *
     * @param userId
     * @param taskId
     * @param request
     * @return
     */

    @Transactional
    public TaskResponse updateTask(Long taskId, Long userId, TaskUpdateRequest request) {
        Task foundTask = taskRepository.findByIdAndTaskOwner_Id(taskId, userId)
                .orElseThrow(() -> new TaskIsNotFoundException("Task is not found"));

        if (hasNoChanges(request, foundTask)) {
            return mapper.toTaskResponse(foundTask);
        }

        if (request.header() != null) {
            foundTask.setHeader(request.header());
        }

        if (request.text() != null) {
            foundTask.setText(request.text());
        }

        if (request.status() != null) {
            updateStatusAndTime(foundTask, request);
        }

        Task updatedTask = taskRepository.save(foundTask);

        return mapper.toTaskResponse(updatedTask);
    }

    private boolean hasNoChanges(TaskUpdateRequest request, Task foundTask) {
        return request.header() == null
                && request.text() == null
                && (request.status() == null
                || request.status() == foundTask.getTaskStatus());
    }

    private void updateStatusAndTime(Task foundTask, TaskUpdateRequest request) {
        TaskStatus currentStatus = foundTask.getTaskStatus();
        TaskStatus newStatus = request.status();
        switch (currentStatus) {
            case CREATED -> {
                if (newStatus == TaskStatus.IN_PROCESS) {
                    foundTask.setTaskStatus(TaskStatus.IN_PROCESS);
                }

                if (newStatus == TaskStatus.FINISHED) {
                    foundTask.setTaskStatus(TaskStatus.FINISHED);
                    foundTask.setFinishedAt(OffsetDateTime.now());
                }
            }

            case IN_PROCESS -> {
                if (newStatus == TaskStatus.CREATED) {
                    throw new IllegalTaskStatusChangeException(
                            String.format(
                                    "Cannot set task status from %s, to %s",
                                    TaskStatus.IN_PROCESS,
                                    TaskStatus.CREATED
                            )
                    );
                }

                if (newStatus == TaskStatus.FINISHED) {
                    foundTask.setTaskStatus(TaskStatus.FINISHED);
                    foundTask.setFinishedAt(OffsetDateTime.now());
                }
            }

            case FINISHED -> {
                if (newStatus != TaskStatus.FINISHED) {
                    throw new IllegalTaskStatusChangeException(
                            String.format(
                                    "Cannot set task status from %s, to %s or %s",
                                    TaskStatus.FINISHED,
                                    TaskStatus.CREATED,
                                    TaskStatus.IN_PROCESS)
                    );
                }
            }
        }
    }

    @Transactional
    public void deleteTask(Long taskId, Long userId) {
        long l = taskRepository.deleteByIdAndTaskOwner_Id(taskId, userId);

        if (l == 0) {
            throw new TaskIsNotFoundException("Task is not found");
        }
    }
}

