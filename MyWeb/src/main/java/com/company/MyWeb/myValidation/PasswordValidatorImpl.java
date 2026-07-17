package com.company.MyWeb.myValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;



/**
 * implementation of the custom validation annotation
 * Specifies the type of field it applies to: String (passwords).
 */
public class PasswordValidatorImpl implements ConstraintValidator<PasswordValidator, String> {

    List<String> weakPasswords;

    @Override
    public void initialize(PasswordValidator constraintAnnotation) {
        weakPasswords = Arrays.asList("123456", "qwer", "asdf");
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            buildViolation(context, "⚠\uFE0F Password cannot be null");
            return false;
        }
        // Check if it's in the weak passwords list
        if (weakPasswords.contains(password)) {
            buildViolation(context, "⚠\uFE0F Password is too common or weak");
            return false;
        }
        // Check for minimum length (e.g., 8 characters)
        if (password.length() < 8) {
            buildViolation(context, "⚠\uFE0F Password must be at least 8 characters long");
            return false;
        }
        return true;
    }

    //disables the default message and adds a custom one
    private void buildViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}