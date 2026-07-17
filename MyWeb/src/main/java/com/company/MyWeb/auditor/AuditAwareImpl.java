package com.company.MyWeb.auditor;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * auditor bean for createdBy and updatedBy based on Authentication
 * How to use it:
 * 1) @EnableJpaAuditing(auditorAwareRef = "auditAwareImpl") on @Configuration class (e.g., @SpringBootApplication)
 * 2) @EntityListeners(AuditingEntityListener.class) on @Entity class
 */
@Component("auditAwareImpl")
public class AuditAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("anonymousUser"); //2) or anonymousUser if authentication not established
        }
        return Optional.of(authentication.getName()); //1) use authentication name as auditor
    }
}
