package com.company.myweb.controller;


import com.company.myweb.model.News;
import com.company.myweb.repository.NewsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static com.company.myweb.constant.ProjectConstant.ANSI_GREEN;
import static com.company.myweb.constant.ProjectConstant.ANSI_RESET;

@Slf4j
@Controller
public class NewsController {

    private final NewsRepository newsRepository;

    @Autowired
    public NewsController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @GetMapping("/news")
    public String newsPage(Model model) {
        List<News> newsList = newsRepository.returnAListOfAllNewsItems(); // 1) 取全部新聞（走 JdbcTemplate，非 JPA）

        // 測試用：把每筆 News 印到 log 觀察 BeanPropertyRowMapper 對映結果
        for (News news : newsList) {
            log.info(ANSI_GREEN + "News: {}", news + ANSI_RESET);
        }

        model.addAttribute("newsList", newsList); // 2) 傳給 Thymeleaf 模板渲染
        return "nav/news";
//        throw new RuntimeException("測試用：故意拋 RuntimeException 觸發 GlobalExceptionHandler");
    }
}
