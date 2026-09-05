package tasksplanner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tasksplanner.request.UserLoginRequest;
import tasksplanner.request.UserRegisterRequest;
import tasksplanner.response.UserResponse;
import tasksplanner.response.UsernameResponse;
import tasksplanner.service.UserService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/user")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody UserRegisterRequest userRegisterRequest
            //сессия ?
    ) {
        UserResponse register = service.register(userRegisterRequest);


        //if (!password.equals(passwordConfirmation)) ... - и на входе проверка 2 полей ДТОхи

        //сессия и безобразие с сеекьюрити, токеном (по идее то же что и с реддисом и хттпсессией)
        //на деле если ОК - будем возвращать токен

        /*
        Зарегистрированный пользователь сразу же автоматически авторизуется, без отдельного заполнения логин формы
В случае успешной регистрации, код ответа HTTP 200, HTTP заголовок ответа содержит выданный пользователю JWT access token
         */

        return ResponseEntity.ok().body(/* token */register);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UsernameResponse> login(
            @Valid @RequestBody UserLoginRequest loginRequest
            //HttpSession session
    ) {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.email(), loginRequest.password());
        Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);

        UserResponse login = service.login(loginRequest);
/*        session.setAttribute("userId", login.id());
        session.setAttribute("username", login.username());*/

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UsernameResponse(login.username()));
    }
}


