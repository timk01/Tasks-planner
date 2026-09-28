package tasksplanner.dto;

import tasksplanner.response.ScheduledTaskResponse;

import java.util.List;

public record UserTasks(
        Long userId,
        String email,
        List<ScheduledTaskResponse> finishedTasks,
        List<ScheduledTaskResponse> unfinishedTasks
) {
}