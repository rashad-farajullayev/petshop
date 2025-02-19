package com.thesniffers.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BasketStatusValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBasketStatus {
    String message() default "Invalid basket status. Allowed values: NEW, PAID, PROCESSED, UNKNOWN";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
