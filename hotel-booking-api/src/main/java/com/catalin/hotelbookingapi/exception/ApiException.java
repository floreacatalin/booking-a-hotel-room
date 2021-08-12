package com.catalin.hotelbookingapi.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {

    public abstract HttpStatus getStatus();
}