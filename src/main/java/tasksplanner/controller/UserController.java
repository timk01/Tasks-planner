package tasksplanner.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tasksplanner.request.UserLoginRequest;
import tasksplanner.request.UserRegisterRequest;
import tasksplanner.response.UserResponse;
import tasksplanner.security.JwtService;
import tasksplanner.service.UserService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/user")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody UserRegisterRequest userRegisterRequest
    ) {
        UserResponse user = service.register(userRegisterRequest);

        Jwt token = jwtService.generateToken(user);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                .body(user);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody UserLoginRequest loginRequest
    ) {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.email(), loginRequest.password());
        Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);

        UserResponse user = service.findUserByEmail(loginRequest.email());

        Jwt token = jwtService.generateToken(user);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                .body(user);
    }
}


