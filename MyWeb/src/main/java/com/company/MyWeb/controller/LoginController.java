package com.company.MyWeb.controller;

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

    //this bypasses the default CSRF protections using Get request
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 1) Get the SecurityContextLogoutHandler object
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

        // 2).logout() clear security context by removing an authentication object, invalidate Http session, delete cookies JSESSIONID
        logoutHandler.logout(request, response, authentication);
        return "redirect:/login?logout=true";
    }
}
