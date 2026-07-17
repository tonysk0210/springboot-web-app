package com.company.MyWeb.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "myweb")
@Data
@Validated
public class MyWebProperties {
    @Min(value = 5, message = "must between 5 and 10")
    @Max(value = 10, message = "must between 5 and 10")
    private int paginationPageSize;
}
