package com.thesniffers.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TimezoneValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTimezone {
    String message() default "Timezone must be valid java zone id";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
