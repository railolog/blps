package ru.ifmo.puls.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.blps.openapi.model.JwtResponseTo;
import ru.blps.openapi.model.SignInRequestTo;
import ru.blps.openapi.model.SignUpRequestTo;
import ru.ifmo.puls.auth.model.Role;
import ru.ifmo.puls.auth.model.User;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtResponseTo signUp(SignUpRequestTo request) {

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getIsSupplier() ? Role.SUPPLIER : Role.USER)
                .build();

        userService.create(user);

        String jwt = jwtService.generateToken(user);
        return new JwtResponseTo().token(jwt);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtResponseTo signIn(SignInRequestTo request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());

        String jwt = jwtService.generateToken(user);
        return new JwtResponseTo().token(jwt);
    }
}
