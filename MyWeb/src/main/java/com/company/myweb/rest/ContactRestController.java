package com.company.myweb.rest;

import com.company.myweb.constant.ProjectConstant;
import com.company.myweb.model.Contact;
import com.company.myweb.repository.ContactRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
 * application/json if the client asks for JSON (via Accept: application/json header).
 * application/xml if the client asks for XML (via Accept: application/xml header).
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/contact", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@CrossOrigin(origins = "*") //allows all domains (e.g., http://localhost:3000, https://example.com, etc.) to access this endpoint.
public class ContactRestController {

    private final ContactRepository contactRepository;

    @Autowired
    public ContactRestController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /**
     * To fetch a list of contact by status using query parameter
     */
    @GetMapping("/getContactMessageByStatus")
    public List<Contact> getContactMessageByStatus(@RequestParam String status) {
        return contactRepository.findByStatus(status);
    }

    /**
     * To fetch a list of contact by status using RequestBody
     */
    /*@RequestBody: "Take the JSON (or XML) from the HTTP request body and map it into a Contact Java object."*/
    @GetMapping("/getContactMessageByStatusRequestBody")
    public List<Contact> getContactMessageByStatusRequestBody(@RequestBody Contact contact) {
        return (contact != null && contact.getStatus() != null) ? contactRepository.findByStatus(contact.getStatus()) : List.of();
    }

    /**
     * This method is to accept HTTP Header (invocationFrom required) and JSON Body mapped to a validated Contact object.
     * Log the request info, then persist the contact.
     * Afterward, return a ResponseEntity wrapping Response pojo
     */
    @PostMapping("/saveContactMessage")
    public ResponseEntity<Response> saveContactMessage(@RequestHeader("invocationFrom") String invocationFrom, @Valid @RequestBody Contact contact) {
        log.info("invocationFrom: {}, contact: {}", invocationFrom, contact);
        contactRepository.save(contact);
        Response response = new Response();
        response.setStatusCode("201");
        response.setStatusMessage("Contact message saved successfully");
        return ResponseEntity.status(HttpStatus.CREATED) //code 201 (Resource created successfully)
                .header("isSaved", "true").body(response); //ResponseBody datatype
    }

    /**
     * This method is to display header info and delete the contact object from RequestEntity<Contact>.
     * Send ResponseEntity<Response> after deletion
     */
    @DeleteMapping("/deleteContactMessage")
    public ResponseEntity<Response> deleteContactMessage(RequestEntity<Contact> requestEntity) {
        //1) iterate all key-value pair elements in http headers and display them
        HttpHeaders httpHeaders = requestEntity.getHeaders(); //HttpHeaders implements Map<String, List<String>>
        httpHeaders.forEach((key, value) -> {
            log.info("key={} : value={}", key, value);
        });

        //2) retrieve contact object through RequestEntity<Contact>
        Contact contact = requestEntity.getBody(); //It deserializes it into a Contact object using Jackson (by default in Spring Boot).

        //3) delete contactMessage via contact
        contactRepository.delete(contact); //equivalent to DELETE FROM contact WHERE contact_id = ? | for .delete() in JPA If contact already deleted	or not found✅ Executes, but does nothing
        //contactRepository.deleteById(contact.getContactId()); would also work and is the better approach

        //4) send ResponseEntity after deletion
        Response response = new Response();
        response.setStatusCode("200");
        response.setStatusMessage("Contact message deleted successfully");
        return ResponseEntity.status(HttpStatus.OK) //code 200 (Request succeeded by default)
                .header("isDeleted", "true").body(response);
    }

    /**
     * This method is to update the contact status given the contactId using @RequestBody.
     * If the contactId given exists, update the status and send isUpdated=true in the Response, or isUpdated=false otherwise.
     */
    @PatchMapping("/updateContactMessageStatus")
    public ResponseEntity<Response> updateContactMessageStatus(@RequestBody Contact contact) {
        Response response = new Response();
        //1) fetch contact object based on getContactId()
        Optional<Contact> contactOptional = contactRepository.findById(contact.getContactId());
        //2) if corresponding contact is found, modify the status and persist
        if (contactOptional.isPresent()) {
            contactOptional.get().setStatus(ProjectConstant.STATUS_CLOSED);
            contactRepository.save(contactOptional.get());
        } else {
            response.setStatusCode("400");
            response.setStatusMessage("Invalid contact ID received");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) //code 400 (Invalid input, Missing required fields, Violated validation rules)
                    .header("isUpdated", "false").body(response);
        }
        //3) send a success ResponseEntity
        response.setStatusCode("200");
        response.setStatusMessage("Message status updated successfully to CLOSED");
        return ResponseEntity.status(HttpStatus.OK).header("isUpdated", "true").body(response);
    }

    /**
     * Spring Data Rest API automatically provided when implementing Spring Data JPA
     * To view other available URI, visit below
     * http://localhost:8081/spring-data-api/profile
     * http://localhost:8081/spring-data-api/ - HAL explorer
     */


}
