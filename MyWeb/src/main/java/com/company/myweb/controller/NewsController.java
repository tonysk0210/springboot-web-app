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
        List<News> newsList = newsRepository.returnAListOfAllNewsItems(); //1) fetch List<News>

        //testing purpose
        for (News news : newsList) {
            log.info(ANSI_GREEN + "News: {}", news + ANSI_RESET);
        }

        model.addAttribute("newsList", newsList); //2) send it to UI
        return "nav/news";
//        throw new RuntimeException("Testing on throwing a RuntimeException");
    }
}
