package com.company.myweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 全域設定
 *
 * 主要用途：註冊「沒有邏輯、單純回傳 view」的路由，避免為此建立空的 @Controller
 * 例如 /about 只是顯示 nav/about.html，不需要任何 controller 邏輯就能透過此處註冊
 */
@Configuration
public class MyWebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // GET /about → 渲染 templates/nav/about.html（無 controller、無業務邏輯）
        registry.addViewController("/about").setViewName("nav/about");
    }
}
