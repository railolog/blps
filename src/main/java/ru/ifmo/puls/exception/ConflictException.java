package ru.ifmo.puls.exception;

import org.springframework.http.HttpStatus;
import ru.ifmo.puls.domain.TenderStatus;

public class ConflictException extends ApiException{
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }

    public static ConflictException incorrectTenderStatus(TenderStatus expected) {
        return new ConflictException("Tender status should be [" + expected + "]");
    }
}
