package com.company.ConsumingRestService.proxy;

import com.company.ConsumingRestService.config.ProjectConfiguration;
import com.company.ConsumingRestService.model.Contact;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * This interface defines the abstract method to call the API endpoint(like JPA). The implementation of the method will complete during the runtime
 * 1. Define @FeignClient interface
 * 2. Define ProjectConfiguration
 * 3. @Autowire ContactProxy to consume the REST API service
 */
@FeignClient(name = "contact-service", url = "http://localhost:8081/api/contact", configuration = ProjectConfiguration.class)
public interface ContactProxy {

    @GetMapping(value = "/getContactMessageByStatus")
    @Headers(value = "Content-Type: application/json")
    public List<Contact> getContactMessageByStatusAPI(@RequestParam String status);
}
