package ru.ifmo.puls.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException{
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public static NotFoundException fromTender(long id) {
        return new NotFoundException("There are no tender with id [" + id + "]");
    }

    public static NotFoundException fromComplaint(long id) {
        return new NotFoundException("There are no complaint_conv with id [" + id + "]");
    }
}
