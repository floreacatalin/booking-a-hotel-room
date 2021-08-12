package com.catalin.hotelbookingapi.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApiException {

    @Override
    public String getMessage() {
        return "api.message.resource-not-found";
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
