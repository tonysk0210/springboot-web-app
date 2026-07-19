package com.company.myweb.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import static com.company.myweb.constant.ProjectConstant.*;

@Slf4j
@Component
@Aspect
public class LoggerAspect {

    /**
     * @Around 環繞通知：切點涵蓋 com.company.MyWeb 底下 class 的方法呼叫。
     *
     * 注意：Spring AOP 是 proxy-based，只攔「經過 bean proxy 的外部呼叫」。以下情況攔不到：
     * - 同 bean 內的 self-invocation（this.method()），因為繞過 proxy
     * - private / static / final 方法（無法被 CGLIB 子類 override）
     * - constructor 內部呼叫（proxy 尚未建立）
     * - 手動 new 出、非 Spring 管理的物件
     *
     * 職責：
     * 1. 進入方法前印出「開始執行」+ 呼叫參數
     * 2. 親自呼叫目標方法（proceed）並計時
     * 3. 離開方法後印出「執行結束」+ 耗時 + 回傳值
     * 4. 把 proceed() 的回傳值原封不動 return 出去
     * （不 return 或 return 別的 → 呼叫者拿不到原本結果，功能會壞）
     */
    @Around("execution(* com.company.myweb..*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // JoinPoint 提供的方法資訊：
        //   getSignature().toShortString() → 簡短簽名，例如 HomeController.homePage()
        //   getArgs()                      → 呼叫時傳入的參數陣列
        //   getTarget()                    → 被代理的原始 bean 實例
        String method = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        String targetClass = joinPoint.getTarget().getClass().getSimpleName();

        log.info(ANSI_PINK + "[{}] {} 開始執行，參數 {}" + ANSI_RESET, targetClass, method, Arrays.toString(args));

        // 1. 記錄方法開始執行的時間
        Instant startTime = Instant.now();
        // 2. 呼叫原始方法
        Object result = joinPoint.proceed(args); // ProceedingJoinPoint 專屬 API：proceed(args) 亦可傳入改動後的參數（此處原封不動傳回）
        // 3. 計算方法執行時間
        long elapsedMs = Duration.between(startTime, Instant.now()).toMillis();

        log.info(ANSI_PINK + "[{}] {} 執行結束，耗時 {} ms，回傳 {}" + ANSI_RESET, targetClass, method, elapsedMs, result);
        return result; // 一定要回傳 proceed() 的結果，否則會中斷原本的方法呼叫鏈與代理機制
    }

    /**
     * @AfterThrowing 後置例外通知：當 com.company.MyWeb 底下方法拋出 Exception 時觸發。
     * 職責：
     * 1. 記錄例外訊息、根本原因與完整 stack trace
     * 2. 只做觀察，不吞例外 — 原本的例外傳播由 Spring 自動處理
     * 3. 用 JoinPoint（非 ProceedingJoinPoint），因為目標方法已經跑完，無法再 proceed
     */
    @AfterThrowing(value = "execution(* com.company.myweb..*.*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        String method = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        Throwable cause = ex.getCause();

        // 三個 {} 分別對應：method / args / message / root cause；最後一個 Throwable 讓 SLF4J 印完整 stack trace
        log.error(ANSI_RED + "{} 拋出例外，參數 {}，訊息 [{}]，根本原因 [{}]" + ANSI_RESET,
                method,
                Arrays.toString(args),
                ex.getMessage(),
                cause != null ? cause.getMessage() : "無",
                ex);
    }
}