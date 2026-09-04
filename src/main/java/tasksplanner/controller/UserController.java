package tasksplanner.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tasksplanner.request.UserRegisterRequest;
import tasksplanner.response.UserResponse;
import tasksplanner.service.UserService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/user")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody UserRegisterRequest userRegisterDto
            //сессия ?
    ) {
        //if (!password.equals(passwordConfirmation)) ... - и на входе проверка 2 полей ДТОхи

        //сессия и безобразие с сеекьюрити, токеном (по идее то же что и с реддисом и хттпсессией)
        UserResponse register = service.register(userRegisterDto);
        //на деле если ОК - будем возвращать токен

        /*
        Зарегистрированный пользователь сразу же автоматически авторизуется, без отдельного заполнения логин формы
В случае успешной регистрации, код ответа HTTP 200, HTTP заголовок ответа содержит выданный пользователю JWT access token
         */

        return ResponseEntity.ok().body(/* token */null);
    }

}
