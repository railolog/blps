package ru.ifmo.puls.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.blps.openapi.api.UserApi;
import ru.blps.openapi.model.UserInfoResponseTo;
import ru.ifmo.puls.auth.model.User;
import ru.ifmo.puls.auth.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final UserService userService;

    @Override
    public ResponseEntity<UserInfoResponseTo> getMeInfo() {
        return ResponseEntity.ok(transform(userService.getCurrentUser()));
    }

    private UserInfoResponseTo transform(User user) {
        return new UserInfoResponseTo()
                .login(user.getUsername());
    }
}
