package com.catalin.hotelbookingapi.validator;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotAboveMaxNoOfBookedDaysValidator implements
        ConstraintValidator<NotAboveMaxNoOfBookedDays, Integer> {

    @Value("${booking.max.length}")
    private int maxBookedDays;

    @Override
    public void initialize(NotAboveMaxNoOfBookedDays constraint) {
    }

    @Override
    public boolean isValid(Integer noOfDays, ConstraintValidatorContext cxt) {
        boolean valid = noOfDays != null && noOfDays > 0 && noOfDays <= maxBookedDays;
        if (!valid) {
            ((ConstraintValidatorContextImpl) cxt).addMessageParameter("0", maxBookedDays);
        }

        return valid;
    }

}