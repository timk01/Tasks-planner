package tasksplanner.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import tasksplanner.entity.Task;
import tasksplanner.response.ScheduledTaskResponse;
import tasksplanner.response.TaskResponse;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface TaskMapper {

    ZoneId MOSCOW_ZONE = ZoneId.of("Europe/Moscow");

    @Mapping(source = "id", target = "taskId")
    @Mapping(source = "taskStatus", target = "status")
    @Mapping(source = "taskOwner.id", target = "ownerId")
    @Mapping(source = "finishedAt", target = "finishedAt", qualifiedByName = "toMoscowOffset")
    TaskResponse toTaskResponse(Task task);

    @Named("toMoscowOffset")
    default OffsetDateTime toMoscowOffset(OffsetDateTime time) {
        return time == null
                ? null
                : time.atZoneSameInstant(ZoneId.of("Europe/Moscow"))
                .toOffsetDateTime();
    }

    List<TaskResponse> toTaskResponseList(List<Task> tasks);

    @Mapping(source = "id", target = "taskId")
    @Mapping(source = "taskStatus", target = "status")
    @Mapping(source = "taskOwner.id", target = "ownerId")
    ScheduledTaskResponse toScheduledTaskResponse(Task task);

    List<ScheduledTaskResponse> toScheduledTaskResponseList(List<Task> tasks);
}
