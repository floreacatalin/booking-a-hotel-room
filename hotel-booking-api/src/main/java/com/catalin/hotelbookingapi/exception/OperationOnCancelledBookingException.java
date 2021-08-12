package com.catalin.hotelbookingapi.exception;


import org.springframework.http.HttpStatus;

public class OperationOnCancelledBookingException extends ApiException {

    @Override
    public String getMessage() {
        return "api.message.operation-on-canceled-booking";
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.PRECONDITION_FAILED;
    }
}
