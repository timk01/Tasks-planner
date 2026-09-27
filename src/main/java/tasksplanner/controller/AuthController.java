package tasksplanner.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tasksplanner.response.UserResponse;
import tasksplanner.security.JwtService;
import tasksplanner.service.UserService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final UserService service;

    @GetMapping("/user")
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String email = jwt.getClaimAsString("email");

        UserResponse user = service.findUserByEmail(email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }
}
