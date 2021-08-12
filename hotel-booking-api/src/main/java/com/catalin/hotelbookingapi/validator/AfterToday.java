package com.catalin.hotelbookingapi.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AfterTodayValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterToday {
    String message() default "{api.message.validation.start-date-earlier-than-tomorrow}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}