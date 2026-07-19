package com.company.myweb.auditor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 稽核 bean：依 Authentication 提供 @CreatedBy / @LastModifiedBy 欄位的值
 * 使用方式：
 * 1) 在 @Configuration class（例如 @SpringBootApplication）上加 @EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
 * 2) 在 @Entity class 上加 @EntityListeners(AuditingEntityListener.class)（或繼承 BaseEntity 由父類統一標註）
 */
@Slf4j
@Component("auditAwareImpl")
public class AuditAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        // 1. 從 SecurityContext 取出 Authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.info("AuditAwareImpl 無有效 Authentication，稽核者退回 anonymousUser");
            return Optional.of("anonymousUser"); // 2) 未認證時退回 anonymousUser
        }
        log.info("AuditAwareImpl 解析出稽核者：{}", authentication.getName());
        // 2. 以 authentication.getName() 作為稽核者名稱
        return Optional.of(authentication.getName()); // 1) 以 authentication.getName() 作為稽核者名稱
    }
}
