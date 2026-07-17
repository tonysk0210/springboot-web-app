package com.company.MyWeb.myValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Why Object? Because this validator is meant to be applied at the class level, because we are comparing two fields of the same object.
 * We’re comparing two fields of the same object, so you don’t need to know its specific type at compile time.
 * Object is preferred over Person when since it can be used across different classes, Person, Employees and so on that gives more flexibility
 */
public class FieldValueMatchValidatorImpl implements ConstraintValidator<FieldValueMatchValidator, Object> {

    private String fieldName;
    private String fieldMatchName;

    @Override
    public void initialize(FieldValueMatchValidator constraintAnnotation) {
        //Captures the field names to compare from the annotation (e.g., password and confirmPassword).
        this.fieldName = constraintAnnotation.field(); //this.field = "password"
        this.fieldMatchName = constraintAnnotation.fieldMatch(); //this.fieldMatch = "confirmPassword"
        //They are just strings representing the names of the fields, not their actual values!
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        //BeanWrapperImpl extracts field values from the target object.
        Object fieldValue = new BeanWrapperImpl(object).getPropertyValue(fieldName); //abc123456789
        Object fieldMatchValue = new BeanWrapperImpl(object).getPropertyValue(fieldMatchName); //abc123456789
        return fieldValue != null && fieldValue.equals(fieldMatchValue);
    }
}
/**
 * +---------------------------------------------------------+
 * |                  UserRegistrationDto (Object)            |
 * |---------------------------------------------------------|
 * | password = "MyPassword123!"                             |
 * | confirmPassword = "MyPassword123!"                      |
 * | email = "user@example.com"                              |
 * | confirmEmail = "user@example.com"                       |
 * +---------------------------------------------------------+
 * |
 * |
 *
 * @FieldValueMatch(field="password", fieldMatch="confirmPassword")
 * |
 * @FieldValueMatch(field="email", fieldMatch="confirmEmail")
 * |
 * V
 * +---------------------------------------------------------+
 * |                 FieldValueMatchValidator                 |
 * |---------------------------------------------------------|
 * | initialize()                                             |
 * |  field = "password"                                      |
 * |  fieldMatch = "confirmPassword"                          |
 * |---------------------------------------------------------|
 * | isValid(Object object)                                   |
 * |   BeanWrapperImpl(object).getPropertyValue("password")   |
 * |     => returns "MyPassword123!"                          |
 * |   BeanWrapperImpl(object).getPropertyValue("confirmPassword")|
 * |     => returns "MyPassword123!"                          |
 * |   Compare: Objects.equals("MyPassword123!", "MyPassword123!")|
 * |     => returns true                                      |
 * |---------------------------------------------------------|
 * | initialize()                                             |
 * |  field = "email"                                         |
 * |  fieldMatch = "confirmEmail"                             |
 * |---------------------------------------------------------|
 * | isValid(Object object)                                   |
 * |   BeanWrapperImpl(object).getPropertyValue("email")      |
 * |     => returns "user@example.com"                        |
 * |   BeanWrapperImpl(object).getPropertyValue("confirmEmail")|
 * |     => returns "user@example.com"                        |
 * |   Compare: Objects.equals("user@example.com", "user@example.com")|
 * |     => returns true                                      |
 * +---------------------------------------------------------+
 */