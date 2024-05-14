package com.wolfcode.eventservice.customValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CategoryValidator.class)
public @interface ValidateCategory {

    String message() default "Choose from: SEMINARS, FUNDRAISERS, COMMUNITY, CONCERT, SPORTS, CULTURAL ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
