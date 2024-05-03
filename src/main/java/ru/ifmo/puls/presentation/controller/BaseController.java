package ru.ifmo.puls.presentation.controller;

import ru.ifmo.puls.auth.service.UserService;
import ru.ifmo.puls.domain.Role;
import ru.ifmo.puls.exception.ForbiddenException;

public abstract class BaseController {
    protected final UserService userService;

    protected BaseController(UserService userService) {
        this.userService = userService;
    }

    protected final void hasRole(Role role) {
        if (userService.getCurrentUser().getRole() != role) {
            throw new ForbiddenException("User hasn't permission to operation");
        }
    }
}
