package com.company.MyWeb.myValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FieldValueMatchValidatorImpl.class)
@Target(ElementType.TYPE) //This annotation is applied at the class level, not field or method level.
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldValueMatchValidator {

    //standard elements
    String message() default "Fields values don't match!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    //custom elements
    String field();

    String fieldMatch();

    /**
     * The inner @interface List within your @FieldValueMatch annotation is a container annotation, and it is used to allow multiple @FieldValueMatch annotations to be applied to the same class.
     * This is necessary because Java does not allow applying the same annotation more than once to the same target by default, unless it's declared as @Repeatable.
     * If you need only one pair of fields to compare (e.g., password/confirmPassword), you don’t need the List wrapper.
     */
    // Container annotation to allow multiple uses of FieldValueMatch on the same class
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        FieldValueMatchValidator[] value();
    }

}
