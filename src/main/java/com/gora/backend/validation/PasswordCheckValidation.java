package com.gora.backend.validation;


import org.apache.commons.lang3.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordCheckValidation implements ConstraintValidator<PasswordCheck, String>   {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(StringUtils.isBlank(value)){
            return false;
        }else{
            return value.length() <= 15;
        }
    }
    
}
