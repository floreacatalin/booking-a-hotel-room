package com.catalin.hotelbookingapi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ApiValidationErrorsDTO {

    private List<String> globalErrors = new ArrayList<>();

    private Map<String, List<String>> fieldErrors = new HashMap<>();

    public ApiValidationErrorsDTO(MethodArgumentNotValidException ex) {
        this.globalErrors = extractGlobalErrorMessages(ex);
        this.fieldErrors = extractFieldErrorMessages(ex);
    }

    private List<String> extractGlobalErrorMessages(MethodArgumentNotValidException ex) {

        return ex.getGlobalErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
    }

    private Map<String, List<String>> extractFieldErrorMessages(MethodArgumentNotValidException ex) {
        Map<String, List<String>> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors();
        ex.getBindingResult().getFieldErrors()
                .forEach(fieldError -> {
                    String field = fieldError.getField();
                    if (!fieldErrors.containsKey(field)) {
                        fieldErrors.put(field, new ArrayList<>());
                    }
                    fieldErrors.get(field).add(fieldError.getDefaultMessage());
                });

        return fieldErrors;
    }

}
