package com.company.myweb.constant;

public interface ProjectConstant {
    String STATUS_OPEN = "OPEN";
    String STATUS_CLOSED = "CLOSED";

    String STUDENT_ROLE = "STUDENT";
    String ADMIN_ROLE = "ADMIN";

    String ANSI_GREEN = "\u001B[32m";
    String ANSI_ORANGE = "\u001B[33;1m";
    String ANSI_RED = "\u001B[31m";
    String ANSI_PINK = "\u001B[95m";       // 給 LoggerAspect 用（bright magenta / 粉紅），跟綠（業務）/紅（錯誤）區分
    String ANSI_RESET = "\u001B[0m";
}