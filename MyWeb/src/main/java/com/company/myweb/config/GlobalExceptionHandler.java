package com.company.myweb.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * MVC 例外處理器 — 攔到 controller 拋出的例外時，統一渲染 errorPage.html
 * <p>
 * 範圍限定：只涵蓋 controller/ 底下（含 controller/authenticated/）的 @Controller
 * - basePackages 限定 → 不會誤攔 REST endpoint 導致回傳 HTML 給 REST client
 * - REST endpoint 的例外由 rest/GlobalExceptionRestController（@RestControllerAdvice）處理
 * - 兩個 handler 各司其職：一個回 HTML view，一個回 JSON body
 */
@ControllerAdvice(basePackages = "com.company.myweb.controller")
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
    /*
     * Spring MVC controller / @ExceptionHandler 常見回傳型別對照：
     *
     *   1. String              → view name 字串，例如 return "home" → 渲染 templates/home.html
     *                            搭配 Model 參數塞資料：public String home(Model model) { model.addAttribute(...); return "home"; }
     *
     *   2. ModelAndView        → view name + model data 打包（本方法採用）
     *                            適合 @ExceptionHandler，因為 Model 參數在 handler 內較難注入，手動 new 最保險
     *
     *   3. void                → 由 controller 直接寫入 HttpServletResponse（少用，通常給 REST 或檔案下載）
     *
     *   4. ResponseEntity<T>   → 完整控制 HTTP 回應（body + header + status），REST 世界的主流
     *                            例：return new ResponseEntity<>(dto, HttpStatus.OK)
     *
     *   5. "redirect:/path"    → 特殊 view name 前綴，觸發 302 重導向
     *                            例：return "redirect:/login" → 302 Location: /login
     *
     *   6. @ResponseBody + Object → 直接把物件序列化成 body（@RestController 已內建此行為）
     *
     *   7. Callable<T> / DeferredResult<T> / Mono<T> → 非同步回應（進階）
     */