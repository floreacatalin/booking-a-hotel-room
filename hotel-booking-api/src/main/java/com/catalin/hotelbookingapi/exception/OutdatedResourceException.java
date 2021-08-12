package com.catalin.hotelbookingapi.exception;

import org.springframework.http.HttpStatus;

public class OutdatedResourceException extends ApiException {

    @Override
    public String getMessage(){
        return "api.message.outdated-resource-exception";
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}
