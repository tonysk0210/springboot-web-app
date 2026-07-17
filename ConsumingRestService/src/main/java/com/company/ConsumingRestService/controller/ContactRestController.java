package com.company.ConsumingRestService.controller;

import com.company.ConsumingRestService.model.Contact;
import com.company.ConsumingRestService.model.Response;
import com.company.ConsumingRestService.proxy.ContactProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class ContactRestController {

    private final ContactProxy contactProxy;
    private final RestTemplate restTemplate;
    private final WebClient webClient;

    @Autowired
    public ContactRestController(ContactProxy contactProxy, RestTemplate restTemplate, WebClient webClient) {
        this.contactProxy = contactProxy;
        this.restTemplate = restTemplate;
        this.webClient = webClient;
    }

    /**
     * http://localhost:8082/getMessages?status=OPEN to retrieve the message with OPEN status
     */
    //via proxy FeignClient
    @GetMapping("/getMessages")
    public List<Contact> getMessages(@RequestParam String status) {
        return contactProxy.getContactMessageByStatusAPI(status);
    }

    /**
     * http://localhost:8082/saveMessages to save the messages with contact fields
     * {
     * "name" : "RestTemplate",
     * "mobile" : "1234567890",
     * "email" : "resttemplate@gmail.com",
     * "subject" : "resttemplate",
     * "message" : "resttemplate message",
     * "status" : "OPEN"
     * }
     */
    //via RestTemplate
    @PostMapping("/saveMessages")
    public ResponseEntity saveMessages(@RequestBody Contact contact) {
        String uri = "http://localhost:8081/api/contact/saveContactMessage";
        //1) Instantiate HttpHeaders with required "invocationFrom"
        HttpHeaders headers = new HttpHeaders();
        headers.add("invocationFrom", "RestTemplate");
        //2) Instantiate ResponseEntity to receive after the operation using an HttpEntity wrapping the body and headers.
        HttpEntity<Contact> httpEntity = new HttpEntity<>(contact, headers);
        //3) Executes the HTTP call
        ResponseEntity<Response> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, Response.class);
        return responseEntity;
    }

    /**
     * http://localhost:8082/saveMessagesWebClient to save to save the messages with contact fields
     * {
     *     "name" : "WebClient",
     *     "mobile" : "1234567890",
     *     "email" : "webclient@gmail.com",
     *     "subject" : "webclient",
     *     "message" : "webclient message",
     *     "status" : "OPEN"
     * }
     */
    //via Webclient
    @PostMapping("/saveMessagesWebClient")
    public Mono<Response> saveMessagesWebClient(@RequestBody Contact contact) {
        String uri = "http://localhost:8081/api/contact/saveContactMessage";
        return webClient.post().uri(uri)
                .header("invocationFrom", "WebClient") //1) Add custom header
                .body(Mono.just(contact), Contact.class) //2) Attach request body
                .retrieve()
                .bodyToMono(Response.class); //3) Extract response as Mono<Response>
    }
}
