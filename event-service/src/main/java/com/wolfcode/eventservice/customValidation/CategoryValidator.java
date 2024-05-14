package com.wolfcode.eventservice.customValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class CategoryValidator implements ConstraintValidator<ValidateCategory, String> {
    @Override
    public boolean isValid(String category, ConstraintValidatorContext constraintValidatorContext) {
        List<String> categoryList = Arrays.asList("CONFERENCES", "SEMINARS", "FUNDRAISERS",
                "COMMUNITY", "CONCERT", "SPORTS", "CULTURAL");
        return categoryList.contains(category);
    }
}
