package tasksplanner.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import tasksplanner.entity.Task;
import tasksplanner.response.TaskResponse;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface TaskMapper {

    @Mapping(source = "id", target = "taskId")
    @Mapping(source = "taskStatus", target = "status")
    @Mapping(source = "taskOwner.id", target = "ownerId")
    TaskResponse toTaskResponse(Task task);

    List<TaskResponse> toTaskResponseList(List<Task> tasks);
}
