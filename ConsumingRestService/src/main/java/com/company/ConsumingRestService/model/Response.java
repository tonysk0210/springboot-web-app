package com.company.ConsumingRestService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This pojo class is created for RestTemplate
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private String statusCode;
    private String statusMessage;
}