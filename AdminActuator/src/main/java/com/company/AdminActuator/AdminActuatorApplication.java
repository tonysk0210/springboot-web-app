package com.company.AdminActuator;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAdminServer //enable admin server
@SpringBootApplication
public class AdminActuatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminActuatorApplication.class, args);
    }

}
