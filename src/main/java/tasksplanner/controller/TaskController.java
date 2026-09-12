package tasksplanner.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tasksplanner.request.TaskCreateRequest;
import tasksplanner.request.TaskUpdateRequest;
import tasksplanner.response.TaskResponse;
import tasksplanner.service.TaskService;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @PostMapping()
    public ResponseEntity<TaskResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TaskCreateRequest taskRequest
    ) {
        long userId = Long.parseLong(Objects.requireNonNull(jwt.getSubject()));

        TaskResponse savedTask = service.createTask(userId, taskRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedTask);
    }

    @GetMapping()
    public ResponseEntity<List<TaskResponse>> getTasks(
            @AuthenticationPrincipal Jwt jwt
    ) {
        long userId = Long.parseLong(Objects.requireNonNull(jwt.getSubject()));

        List<TaskResponse> tasks = service.getUserTasks(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tasks);
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskResponse> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest taskUpdateRequest
    ) {
        long userId = Long.parseLong(Objects.requireNonNull(jwt.getSubject()));

        TaskResponse updatedTask = service.updateTask(taskId, userId, taskUpdateRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long taskId
    ) {
        long userId = Long.parseLong(Objects.requireNonNull(jwt.getSubject()));

        service.deleteTask(taskId, userId);

        return ResponseEntity
                .noContent()
                .build();
    }
}
