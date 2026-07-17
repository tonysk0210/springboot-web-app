package com.company.ConsumingRestService.model;

import lombok.Data;

/**
 * This pojo class is created for RestTemplate
 */
@Data
public class Contact {

    private int contactId;
    private String name;
    private String mobile;
    private String email;
    private String subject;
    private String message;
    private String status;

}