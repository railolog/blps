package ru.ifmo.puls.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.blps.openapi.api.UserApi;
import ru.blps.openapi.model.UserStatsResponseTo;
import ru.ifmo.puls.service.UserStatsService;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final UserStatsService userStatsService;

    @Override
    public ResponseEntity<UserStatsResponseTo> getUserStats(Long id) {
        return ResponseEntity.ok(userStatsService.getUserStats(id));
    }
}
