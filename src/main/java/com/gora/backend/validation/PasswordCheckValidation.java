package com.gora.backend.validation;

import java.util.Optional;

import com.mysql.cj.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordCheckValidation implements ConstraintValidator<PasswordCheck, String>   {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(StringUtils.isEmptyOrWhitespaceOnly(value)){
            return false;
        }else{
            return value.length() <= 15;
        }
    }
    
}
