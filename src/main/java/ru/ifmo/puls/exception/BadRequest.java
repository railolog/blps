package ru.ifmo.puls.exception;

import org.springframework.http.HttpStatus;

public class BadRequest extends ApiException {
    public BadRequest(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
