package tasksplanner.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tasksplanner.dto.UserTasks;
import tasksplanner.response.TaskResponse;
import tasksplanner.service.UserTasksService;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

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
            //@NotBlank
/*            @Pattern(
                    regexp = PATH_POST_STRICT_VALIDATOR_REGEXP,
                    message = WRONG_PATH
            )*/
                    //toDo проверки ?
                    Instant from,

            @RequestParam("to")
            //@NotBlank
/*            @Pattern(
                    regexp = PATH_POST_STRICT_VALIDATOR_REGEXP,
                    message = WRONG_PATH
            )*/
                    Instant to
    ) {
        List<UserTasks> tasks = service.getUserTasks(from, to);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tasks);
    }
}
