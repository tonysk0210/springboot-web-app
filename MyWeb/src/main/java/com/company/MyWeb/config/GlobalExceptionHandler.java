package com.company.MyWeb.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/*@ControllerAdvice - a specialized @Component in Spring MVC that allows you to
1. Handle exceptions globally (not just in one controller).
2. Define global model attributes shared across controllers.
3. Define global data binding rules (e.g., @InitBinder).*/

@ControllerAdvice(basePackages = "com.company.myweb.controller", assignableTypes = com.company.MyWeb.config.MyWebConfig.class) //applied to com.company.myweb.controller and WebConfig class
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        ModelAndView modelAndView = new ModelAndView(); //ModelAndView is not automatically injected by the framework, but Model is.
        modelAndView.setViewName("errorPage");
        modelAndView.addObject("exceptionMessage", ex.getMessage());
        return modelAndView;
    }
}

/*Request → DispatcherServlet → Controller → Exception Thrown
         ↓
@ControllerAdvice / @ExceptionHandler → handleException(ex)
         ↓
ModelAndView (errorPage + errorMsg) → View Resolver → Render errorPage*/