package com.company.MyWeb;

import com.company.MyWeb.config.MyWebProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl") // 啟用 JPA 稽核（application 層級），由 auditAwareImpl 提供當前使用者名稱給 @CreatedBy / @LastModifiedBy
@EnableConfigurationProperties(MyWebProperties.class) // 顯式登記 @ConfigurationProperties 類別為 bean（取代在 MyWebProperties 上加 @Component）
public class MyWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyWebApplication.class, args);
    }
}
