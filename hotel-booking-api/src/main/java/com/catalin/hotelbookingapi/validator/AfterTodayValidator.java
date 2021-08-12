package com.catalin.hotelbookingapi.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class AfterTodayValidator implements
        ConstraintValidator<AfterToday, LocalDate> {

    @Override
    public void initialize(AfterToday constraint) {
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext cxt) {

        return date.isAfter(LocalDate.now());
    }

}