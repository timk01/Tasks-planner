package tasksplanner.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tasksplanner.dto.UserTasks;
import tasksplanner.exception.managed.IllegalDateException;
import tasksplanner.service.UserTasksService;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/tasks/getScheduledTasks")
@RequiredArgsConstructor
public class UserTasksController {

    private final UserTasksService service;

    @GetMapping()
    public ResponseEntity<List<UserTasks>> getUsersTasks(
            /* @AuthenticationPrincipal Jwt jwt,*/
            //toDo
            //здесь ТОЧНО будет какой-то токен, иначе мы ломимся из стороннего сервиса
            //в наш и хватаем защищенные данные просто так.

            @RequestParam("from")
            Instant from,
            @RequestParam("to")
            Instant to
    ) {
        if (!from.isBefore(to)) {
            throw new IllegalDateException(
                    String.format(
                            "From date %s must be before to date %s",
                            from,
                            to)
            );
        }

        List<UserTasks> tasks = service.getUserTasks(from, to);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tasks);
    }
}
