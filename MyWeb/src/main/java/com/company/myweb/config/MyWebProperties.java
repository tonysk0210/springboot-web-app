package com.company.myweb.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * MyWeb 可調參數集中管理。
 * 新加 myweb.* 屬性一律放這裡，不要用 @Value 散在各處。
 *
 * @ConfigurationProperties("myweb") 綁 application.properties 內 myweb.* 開頭的屬性
 * @Validated 啟動時做 Bean Validation，範圍不合 app 直接啟動失敗
 * <p>
 * 此 class 為純 POJO，不加 @Component；
 * 由 MyWebApplication 上的 @EnableConfigurationProperties(MyWebProperties.class) 顯式登記為 bean。
 */
@ConfigurationProperties(prefix = "myweb")
@Data
@Validated
public class MyWebProperties {

    // 後台聯絡訊息列表每頁筆數（ContactService 使用），合法範圍 5~10
    @Min(value = 5, message = "數值必須介於 5 到 10 之間")
    @Max(value = 10, message = "數值必須介於 5 到 10 之間")
    private int paginationPageSize;
}
