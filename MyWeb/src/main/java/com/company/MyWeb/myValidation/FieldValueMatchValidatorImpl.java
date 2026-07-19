package com.company.MyWeb.myValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

/**
 * @FieldValueMatchValidator 的實作 — 檢查同一物件內指定的兩個 field 值是否相等
 *
 * 為什麼泛型第二個參數是 Object（而非 Person）：
 *   - 這個 validator 需要拿「整個物件」去比對兩個 field（跨欄位驗證）
 *   - 不同 class（Person / RegisterDto / 其他）都可能套用，用 Object 讓 validator 通用
 *   - 具體型別編譯期不知道 → 只能用反射（BeanWrapperImpl）取欄位值
 *
 * ConstraintValidator 介面要求兩個方法：
 *   - initialize(annotation)：validator 建立時呼叫一次，讀出註解上的參數存進 field
 *   - isValid(object, ctx)：每次驗證觸發時呼叫，回 true/false
 */
public class FieldValueMatchValidatorImpl implements ConstraintValidator<FieldValueMatchValidator, Object> {

    private String fieldName;
    private String fieldMatchName;

    @Override
    public void initialize(FieldValueMatchValidator constraintAnnotation) {
        // 讀出註解上指定的兩個欄位名稱（純字串，不是實際值）
        this.fieldName = constraintAnnotation.field();           // 例："password"
        this.fieldMatchName = constraintAnnotation.fieldMatch(); // 例："confirmPassword"
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        // 用 Spring 的 BeanWrapperImpl 反射取值（背後呼叫對應的 getter）
        Object fieldValue = new BeanWrapperImpl(object).getPropertyValue(fieldName);
        Object fieldMatchValue = new BeanWrapperImpl(object).getPropertyValue(fieldMatchName);
        // 兩個值必須非 null 且相等
        return fieldValue != null && fieldValue.equals(fieldMatchValue);
    }
}
/*
 * 執行時流程圖示：
 *
 * +---------------------------------------------------------+
 * |            Person（或任何套用此註解的物件）              |
 * |---------------------------------------------------------|
 * | password        = "MyPassword123!"                      |
 * | confirmPassword = "MyPassword123!"                      |
 * | email           = "user@example.com"                    |
 * | confirmEmail    = "user@example.com"                    |
 * +---------------------------------------------------------+
 *              |
 *              v
 * @FieldValueMatchValidator(field="password", fieldMatch="confirmPassword")
 * @FieldValueMatchValidator(field="email",    fieldMatch="confirmEmail")
 *              |
 *              v
 * +---------------------------------------------------------+
 * |          FieldValueMatchValidatorImpl                   |
 * |---------------------------------------------------------|
 * | 第一次 initialize(): field=password, fieldMatch=confirmPassword
 * | isValid(person):                                        |
 * |   BeanWrapperImpl 讀 "password"        -> "MyPassword123!"
 * |   BeanWrapperImpl 讀 "confirmPassword" -> "MyPassword123!"
 * |   equals -> true -> 通過                                |
 * |---------------------------------------------------------|
 * | 第二次 initialize(): field=email, fieldMatch=confirmEmail
 * | isValid(person):                                        |
 * |   BeanWrapperImpl 讀 "email"        -> "user@example.com"
 * |   BeanWrapperImpl 讀 "confirmEmail" -> "user@example.com"
 * |   equals -> true -> 通過                                |
 * +---------------------------------------------------------+
 */
