package ru.ifmo.puls.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.blps.openapi.api.AuthApi;
import ru.blps.openapi.model.JwtResponseTo;
import ru.blps.openapi.model.SignInRequestTo;
import ru.blps.openapi.model.SignUpRequestTo;
import ru.ifmo.puls.auth.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    private final AuthenticationService authenticationService;

    @Override
    public ResponseEntity<JwtResponseTo> signIn(SignInRequestTo signInRequestTo) {
        return ResponseEntity.ok(authenticationService.signIn(signInRequestTo));
    }

    @Override
    public ResponseEntity<JwtResponseTo> signUp(SignUpRequestTo signUpRequestTo) {
        return ResponseEntity.ok(authenticationService.signUp(signUpRequestTo));
    }
}
