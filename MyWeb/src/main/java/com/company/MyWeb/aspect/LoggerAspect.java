package com.company.MyWeb.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

import static com.company.MyWeb.constant.ProjectConstant.*;

@Slf4j
@Aspect
@Component
public class LoggerAspect {


    //Not returning the result of proceed() can break method calls and proxying.
    @Around("execution(* com.company.MyWeb..*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info(ANSI_ORANGE + joinPoint.getSignature().toString() + "method execution starts" + ANSI_RESET);
        Instant startTime = Instant.now();
        Object result = joinPoint.proceed();
        Instant endTime = Instant.now();
        long timeElapsed = Duration.between(startTime, endTime).toMillis();
        log.info(ANSI_ORANGE + joinPoint.getSignature().toString() + "method execution ends in " + timeElapsed + " ms" + ANSI_RESET);
        return result;
    }

    @AfterThrowing(value = "execution(* com.company.MyWeb..*.*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        log.error(ANSI_RED + joinPoint.getSignature().toString() + "An Exception happened due to: ", ex.getMessage() + ". and its caused: " + ((ex.getCause() != null) ? ex.getCause().getMessage() : null) + ANSI_RESET);
    }

}
/* spring-boot-starter-web and spring-boot-starter-security transitively include spring-aop and related dependencies
 * Without spring-boot-starter-aop or @EnableAspectJAutoProxy, the proxy creation behavior may not cover everything*/