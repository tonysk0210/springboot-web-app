package com.company.MyWeb.myValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * define a custom validation annotation called @PasswordStrength
 * These are the three standard elements required for any custom constraint annotation in Java Bean Validation (JSR 380).
 */
@Constraint(validatedBy = PasswordValidatorImpl.class) // This tells Bean Validation (JSR 380) to use the PasswordValidator class to handle validation logic for this annotation.
@Target({ElementType.METHOD, ElementType.FIELD}) //this annotation can be applied to methods or fields
@Retention(RetentionPolicy.RUNTIME) //Ensures the annotation is retained at runtime so that the validation framework can access it during validation.
public @interface PasswordValidator {
    String message() default "Please choose a stronger password"; //The default error message returned when validation fails.

    Class<?>[] groups() default {}; //Supports validation groups (advanced usage), allowing different validation rules for different contexts.

    Class<? extends Payload>[] payload() default {}; //For metadata payloads, often used for attaching severity levels or custom hints.
}
