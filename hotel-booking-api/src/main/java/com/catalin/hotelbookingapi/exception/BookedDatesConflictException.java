package com.catalin.hotelbookingapi.exception;

import org.springframework.http.HttpStatus;

public class BookedDatesConflictException extends ApiException {

    @Override
    public String getMessage() {
        return "api.message.booked-dates-conflict";
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}
