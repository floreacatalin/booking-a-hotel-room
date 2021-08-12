package com.catalin.hotelbookingapi.validator;

import com.catalin.hotelbookingapi.dto.BookingSaveDTO;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class WithinTheAvailabilityPeriodValidator implements
        ConstraintValidator<WithinTheAvailabilityPeriod, BookingSaveDTO> {

    @Value("${room.max.availability}")
    private int roomMaxAvailability;

    @Override
    public void initialize(WithinTheAvailabilityPeriod constraint) {
    }

    @Override
    public boolean isValid(BookingSaveDTO bookingSaveDTO, ConstraintValidatorContext cxt) {
        boolean valid = bookingSaveDTO.getStartDate().isBefore(LocalDate.now()
                .plusDays(roomMaxAvailability - bookingSaveDTO.getNoOfDays() + 2));
        if (!valid) {
            ((ConstraintValidatorContextImpl) cxt).addMessageParameter("0", roomMaxAvailability);
        }

        return valid;
    }

}