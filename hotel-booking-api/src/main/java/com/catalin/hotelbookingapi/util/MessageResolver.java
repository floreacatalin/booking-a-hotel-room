package com.catalin.hotelbookingapi.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageResolver {

    private final MessageSource messageSource;

    public String resolveMessage(String code, String... params) {
        return messageSource.getMessage(code, params, null);
    }
}
