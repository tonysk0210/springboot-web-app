package com.company.MyWeb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication //with @Configuration and @ComponentScan
@EnableAspectJAutoProxy //Enables support for handling components marked with @Aspect via Spring AOP proxies.
@EnableJpaRepositories("com.company.MyWeb.repository")//This tells Spring to scan the specified package and its subpackages for Spring Data JPA repository interfaces.
@EntityScan(basePackages = "com.company.MyWeb.model") //This tells Spring to scan the specified package and its subpackages for JPA entity classes annotated with @Entity, @Embeddable, or @MappedSuperclass.

@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl") //enable JPA auditing - application level
public class MyWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyWebApplication.class, args);
    }
}
