package com.company.ConsumingRestService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.company.ConsumingRestService.proxy") //Identifies where the ContactProxy interface is located
public class ConsumingRestServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumingRestServiceApplication.class, args);
    }

}
