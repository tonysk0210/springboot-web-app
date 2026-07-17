package com.company.ConsumingRestService.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ProjectConfiguration {

    //Authorization setup for FeignClient
    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("admin@gmail.com", "admin");
    }

    //Authorization setup for RestTemplate
    @Bean
    public RestTemplate restTemplate() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        return restTemplateBuilder.basicAuthentication("admin@gmail.com", "admin").build();
    }

    //Authorization set up for WebClient
    @Bean
    public WebClient webClient() {
        return WebClient.builder().filter(ExchangeFilterFunctions.basicAuthentication("admin@gmail.com", "admin")).build();
    }


}
