package com.catalin.hotelbookingclient.rest;

import feign.FeignException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public String handleFeignStatusException(FeignException e, HttpServletResponse response) throws IOException {
        response.setStatus(e.status());
        return e.responseBody()
                .map(StandardCharsets.UTF_8::decode)
                .map(CharBuffer::toString)
                .orElse(null);
    }

}