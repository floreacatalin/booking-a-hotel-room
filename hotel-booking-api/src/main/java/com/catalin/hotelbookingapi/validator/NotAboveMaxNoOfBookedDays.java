package com.catalin.hotelbookingapi.validator;

        import javax.validation.Constraint;
        import javax.validation.Payload;
        import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotAboveMaxNoOfBookedDaysValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotAboveMaxNoOfBookedDays {
    String message() default "{api.message.validation.above-max-no-of-booked-days}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}