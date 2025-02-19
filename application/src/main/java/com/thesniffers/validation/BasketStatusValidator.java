package com.thesniffers.validation;

import com.thesniffers.dao.model.BasketStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BasketStatusValidator implements ConstraintValidator<ValidBasketStatus, String> {

    private String allowedValues;

    @Override
    public void initialize(ValidBasketStatus constraintAnnotation) {
        allowedValues = Arrays.stream(BasketStatus.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));  // Generates: "NEW, PAID, PROCESSED, UNKNOWN"
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;  // Ensures "status" cannot be null
        }

        boolean isValid = Arrays.stream(BasketStatus.values())
                .anyMatch(status -> status.name().equalsIgnoreCase(value));

        if (!isValid) {
            // Customizing validation message
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Invalid basket status. Allowed values: " + allowedValues
            ).addConstraintViolation();
        }

        return isValid;
    }
}