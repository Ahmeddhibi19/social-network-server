package com.Ahmed.validator;

import com.Ahmed.annotation.ValidPassword;
import com.Ahmed.common.AppConstants;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password != null) {
            Pattern pattern = Pattern.compile(AppConstants.PASSWORD_PATTERN);
            Matcher matcher = pattern.matcher(password);
            return matcher.matches();
        }
        return false;
    }
}
