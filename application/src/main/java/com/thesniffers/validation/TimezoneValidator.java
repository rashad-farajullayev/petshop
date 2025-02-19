package com.thesniffers.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.ZoneId;

public class TimezoneValidator implements ConstraintValidator<ValidTimezone, String> {

    @Override
    public boolean isValid(String timezone, ConstraintValidatorContext context) {
        if (timezone == null || timezone.isBlank()) {
            return false;
        }
        return ZoneId.getAvailableZoneIds().contains(timezone);
    }
}
