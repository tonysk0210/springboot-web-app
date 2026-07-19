package com.company.myweb.myValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自訂 Bean Validation 註解 — 檢查密碼強度（非 null、非弱密碼清單、長度 >= 8）
 *
 * 這是「單欄位」驗證註解（field-level），對照 @FieldValueMatchValidator 是「跨欄位」（class-level）
 * 用法：貼在 String 型別的密碼欄位上（例如 Person.password），Bean Validation 會自動掃到並呼叫實作
 *
 * 自訂註解的三段式機制細節請看 FieldValueMatchValidator 的 Javadoc，本 codebase 內兩個自訂註解走同套路
 */
@Constraint(validatedBy = PasswordValidatorImpl.class) // 綁定實作類別
@Target({ElementType.METHOD, ElementType.FIELD})       // 可貼在方法或欄位上（field-level）
@Retention(RetentionPolicy.RUNTIME)                    // 執行時保留，反射才讀得到
public @interface PasswordValidator {
    // ===== JSR-380 必要元素 =====
    String message() default "Please choose a stronger password"; // 預設訊息（會被 Impl 內的 buildViolation 動態覆寫）
    Class<?>[] groups() default {};                                // 分組驗證
    Class<? extends Payload>[] payload() default {};               // 附加 metadata
}
