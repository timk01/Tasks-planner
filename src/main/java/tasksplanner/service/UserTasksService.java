package tasksplanner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tasksplanner.dto.UserTasks;
import tasksplanner.entity.Task;
import tasksplanner.entity.TaskStatus;
import tasksplanner.mapper.TaskMapper;
import tasksplanner.repository.TaskRepository;
import tasksplanner.response.TaskResponse;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserTasksService {

    private final TaskRepository taskRepository;
    private final TaskMapper mapper;

    @Transactional
    public List<UserTasks> getUserTasks(Instant from, Instant to) {
        OffsetDateTime fromOffset = from.atOffset(ZoneOffset.UTC);
        OffsetDateTime toOffset = to.atOffset(ZoneOffset.UTC);

        List<Task> finishedTasks
                = taskRepository.findAllFinishedTasksByOwners(TaskStatus.FINISHED, fromOffset, toOffset);

        List<Task> unfinishedTasks
                = taskRepository.findAllUnfinishedTasksByOwners(TaskStatus.FINISHED);

        Map<Long, UserTasks> userTasks = new HashMap<>();

        extracted(finishedTasks, userTasks, true);
        extracted(unfinishedTasks, userTasks, false);

        return new ArrayList<>(userTasks.values());
    }

    private void extracted(List<Task> tasks,
                           Map<Long, UserTasks> userTasks,
                           boolean finished) {
        for (Task task : tasks) {
            Long ownerId = task.getTaskOwner().getId();
            String email = task.getTaskOwner().getEmail();

            userTasks.putIfAbsent(
                    ownerId,
                    new UserTasks(
                            ownerId,
                            email,
                            new ArrayList<>(),
                            new ArrayList<>()
                    )
            );

            TaskResponse taskResponse = mapper.toTaskResponse(task);
            if (finished) {
                userTasks.get(ownerId).finishedTasks().add(taskResponse);
            } else {
                userTasks.get(ownerId).unfinishedTasks().add(taskResponse);
            }
        }
    }
}
