package com.company.myweb.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO（Data Transfer Object）— 用於「修改個人資料」流程中暫存表單資料 + 進行 Bean Validation
 * 非 JPA Entity（沒 @Entity、不入庫，也不繼承 BaseEntity）
 * 在 ProfilePageController 內接收表單輸入，經驗證後由 controller/service 手動搬到 Person Entity 存 DB
 *
 * 這是驗證註解「應放 DTO 而非 Entity」的正確示範，其他 Entity 上的驗證註解為權宜作法
 */
@Data
public class Profile {
    @NotBlank(message = "⚠\uFE0F 姓名不可為空")
    private String name;

    @NotBlank(message = "⚠\uFE0F 手機號碼必填")
    @Pattern(regexp = "[0-9]{10}", message = "⚠\uFE0F 手機號碼必須為 10 位數字")
    private String mobile;

    @NotBlank(message = "⚠\uFE0F Email 必填")
    private String email;

    @NotBlank(message = "⚠\uFE0F 地址第一行必填")
    private String address1;

    private String address2;

    @NotBlank(message = "⚠\uFE0F 城市必填")
    private String city;

    @NotBlank(message = "⚠\uFE0F 郵遞區號必填")
    @Pattern(regexp = "[0-9]{3}", message = "⚠\uFE0F 郵遞區號必須為 3 位數字")
    private String zipcode;
}
