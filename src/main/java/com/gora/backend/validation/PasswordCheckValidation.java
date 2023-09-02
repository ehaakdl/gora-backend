package com.gora.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordCheckValidation implements ConstraintValidator<PasswordCheck, String>   {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.length() <= 15;
    }
    
}
