package com.catalin.hotelbookingapi.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = WithinTheAvailabilityPeriodValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithinTheAvailabilityPeriod {
    String message() default "{api.message.validation.booking-outside-availability-period}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}