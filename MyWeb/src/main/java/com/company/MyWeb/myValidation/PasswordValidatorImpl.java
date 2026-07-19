package com.company.MyWeb.myValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;



/**
 * @PasswordValidator 的實作 — 檢查密碼強度
 *
 * 為什麼泛型第二個參數是 String（而非 Object）：
 *   - 這個 validator 只驗「單一欄位（字串型別）」，不需要整個物件
 *   - 貼註解時 Bean Validation 直接把該 field 的值傳進 isValid()
 *
 * 三種失敗情境對應不同錯誤訊息（都靠 buildViolation() 動態覆寫預設訊息）：
 *   1. password 為 null
 *   2. 屬於弱密碼清單（123456 之類）
 *   3. 長度不足 8
 * 比註解上寫死的 message="..." 更靈活，可依情況給不同提示
 */
public class PasswordValidatorImpl implements ConstraintValidator<PasswordValidator, String> {

    List<String> weakPasswords;

    @Override
    public void initialize(PasswordValidator constraintAnnotation) {
        // 初始化弱密碼清單（validator 建立時只跑一次）
        weakPasswords = Arrays.asList("123456", "qwer", "asdf");
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            buildViolation(context, "⚠\uFE0F 密碼不可為空");
            return false;
        }
        // 檢查是否在弱密碼清單內
        if (weakPasswords.contains(password)) {
            buildViolation(context, "⚠\uFE0F 密碼過於簡單或常見");
            return false;
        }
        // 檢查最小長度（8 字元）
        if (password.length() < 8) {
            buildViolation(context, "⚠\uFE0F 密碼長度需至少 8 個字元");
            return false;
        }
        return true;
    }

    /**
     * 動態覆寫錯誤訊息 — 比註解上寫死的 message="..." 更靈活
     * 適用「同一個 validator 依情況給不同訊息」的場景（例如上面三種失敗各給不同提示）
     */
    private void buildViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();          // 關掉註解上的預設訊息
        context.buildConstraintViolationWithTemplate(message) // 使用自訂訊息
                .addConstraintViolation();
    }
}