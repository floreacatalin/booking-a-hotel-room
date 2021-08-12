package com.catalin.hotelbookingapi.rest;

import com.catalin.hotelbookingapi.dto.ApiValidationErrorsDTO;
import com.catalin.hotelbookingapi.exception.ApiException;
import com.catalin.hotelbookingapi.util.MessageResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class RestResponseExceptionHandler {

    private final MessageResolver messageResolver;

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> handleGenericExceptions(Exception ex) {
        String message = messageResolver.resolveMessage("api.message.internal-issue");

        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = OptimisticLockingFailureException.class)
    public ResponseEntity<String> handleGenericExceptions(OptimisticLockingFailureException ex) {
        String message = messageResolver.resolveMessage("api.message.optimistic-locking-exception");

        return new ResponseEntity<>(message, HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<String> handleApiException(ApiException ex) {
        String message = messageResolver.resolveMessage(ex.getMessage());

        return new ResponseEntity<>(message, ex.getStatus());
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleWrongMessageFormat(HttpMessageNotReadableException ex) {

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiValidationErrorsDTO> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        return new ResponseEntity<>(new ApiValidationErrorsDTO(ex), HttpStatus.BAD_REQUEST);
    }

}