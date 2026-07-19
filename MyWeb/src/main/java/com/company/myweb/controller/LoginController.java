package com.company.myweb.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "nav/login";
    }

    // 用 GET 繞過 Spring 預設對 POST /logout 的 CSRF 檢查（登出這種操作 GET 也可接受）
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 1) 建立 Spring 內建的登出處理器
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

        // 2) 執行登出：清除 SecurityContext、invalidate HttpSession、刪除 JSESSIONID cookie
        logoutHandler.logout(request, response, authentication);
        return "redirect:/login?logout=true";
    }
}
