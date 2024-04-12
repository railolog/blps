package ru.ifmo.puls.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApiException{
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

    public static ForbiddenException fromUserId(long userId) {
        return new ForbiddenException("Operation is forbidden for user with userid [" + userId + "]");
    }
}
