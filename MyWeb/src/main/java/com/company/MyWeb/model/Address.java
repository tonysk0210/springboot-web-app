package com.company.MyWeb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 地址 Entity — 對應 DB 表 `address`（schema.sql）
 * 由 Person 單向擁有（Person.address 持有外鍵 address_id）；本身無反向關聯欄位
 * 註：欄位上的 @NotBlank / @Pattern 為表單直接綁 Entity 的權宜作法，
 * 嚴謹寫法應把驗證搬到 DTO 層（見 Profile.java），Entity 只負責 persistence
 */
@Getter
@Setter
@Entity
@ToString(callSuper = true)
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int addressId;

    @NotBlank(message = "地址第一行必填")
    private String address1;

    private String address2;

    @NotBlank(message = "城市必填")
    private String city;

    @NotBlank(message = "郵遞區號必填")
    @Pattern(regexp = "[0-9]{3}", message = "郵遞區號必須為 3 位數字")
    private String zipCode;
}
