package tasksplanner.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tasksplanner.request.TaskRequest;
import tasksplanner.request.TaskUpdateRequest;
import tasksplanner.request.UserLoginRequest;
import tasksplanner.response.TaskResponse;
import tasksplanner.response.UserResponse;
import tasksplanner.service.TaskService;

import java.util.Objects;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @PostMapping()
    public ResponseEntity<TaskResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TaskRequest taskRequest
    ) {
        long userId = Long.parseLong(Objects.requireNonNull(jwt.getSubject()));

        TaskResponse savedTask = service.createTask(userId, taskRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedTask);
    }


    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskResponse> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest taskUpdateRequest
    ) {
        long userId = Long.parseLong(Objects.requireNonNull(jwt.getSubject()));

        TaskResponse updatedTask = service.updateTask(userId, taskId, taskUpdateRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedTask);
    }
}
