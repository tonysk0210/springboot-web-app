package com.company.myweb.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * 全域例外處理器 — 攔到 controller 拋出的例外時，統一渲染 errorPage.html
 *
 * ⚠ 目前 @ControllerAdvice 沒設 filter，會同時套用到 @Controller 與 @RestController。
 *   REST endpoint 拋例外時，此處回 ModelAndView（HTML 頁面）會不符合 REST client 期待。
 *   若想只涵蓋 MVC，需加 basePackages 或 annotations filter 限定範圍。
 *   REST 專用的錯誤處理在 rest/GlobalExceptionRestController（@RestControllerAdvice）。
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        // ModelAndView 需手動 new（不像 Model 會被框架自動注入）
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("errorPage");                       // → templates/errorPage.html
        modelAndView.addObject("exceptionMessage", ex.getMessage()); // → 傳給模板顯示
        return modelAndView;
    }
}
