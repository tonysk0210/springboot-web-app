package com.company.myweb.myValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自訂 Bean Validation 註解 — 檢查同一物件內兩個 field 值是否相等（跨欄位驗證）
 *
 * ===== 自訂 Bean Validation 的三段式機制 =====
 * 想加一個 JSR-380（jakarta.validation）認得的自訂驗證註解，必須寫三件事：
 *   1. 註解定義（本檔）— 一個 @interface，帶 3 個必要元素：message / groups / payload
 *   2. 綁定實作 — 用 @Constraint(validatedBy = ??Impl.class) 指定實作類別
 *   3. 實作類別（見 FieldValueMatchValidatorImpl）— 實作 ConstraintValidator 介面的 initialize + isValid
 *
 * 執行時機（如何被套用）：
 *   controller 標 @Valid 觸發 Bean Validation
 *     → Spring 掃 Person 上所有 constraint 註解
 *     → 找到 @FieldValueMatchValidator
 *     → 依 @Constraint 找到 FieldValueMatchValidatorImpl
 *     → 呼叫 Impl.isValid(person, ctx)
 *     → 回 false 就把 message 塞進 BindingResult 錯誤清單
 *     → controller 檢查 result.hasErrors() → 回註冊頁顯示錯誤
 *
 * 本註解的兩個特別點：
 *   - @Target(TYPE)：標在 class 而非 field（跨欄位驗證需拿整個物件）
 *   - .List 容器：Java 預設不允許同註解在同目標重複貼，需用容器包（新寫法可改用 @Repeatable）
 */
@Constraint(validatedBy = FieldValueMatchValidatorImpl.class) // 綁定實作類別
@Target(ElementType.TYPE) // 標在 class 層級（跨欄位驗證）
@Retention(RetentionPolicy.RUNTIME) // 執行時保留 → 反射才讀得到
public @interface FieldValueMatchValidator {

    // ===== JSR-380 必要元素（每個 constraint 都要有）=====
    String message() default "欄位值不一致"; // 驗證失敗時的預設訊息（實際使用時通常會被呼叫端覆寫，例如 Person 上的 "⚠️ 密碼與確認密碼不一致"）
    Class<?>[] groups() default {};                        // 分組驗證（例如註冊 vs 更新用不同組）
    Class<? extends Payload>[] payload() default {};       // 附加 metadata（罕用）

    // ===== 本註解自訂的元素（透過 initialize() 傳入 Impl）=====
    String field();       // 第一個要比對的 property 名稱（例："password"）
    String fieldMatch();  // 第二個要比對的 property 名稱（例："confirmPassword"）

    /**
     * .List — 容器註解，讓同一個 class 可貼多個 @FieldValueMatchValidator。
     * 若只需比對一對欄位，直接 @FieldValueMatchValidator(...) 即可，不用包 List。
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        FieldValueMatchValidator[] value();
    }
}
