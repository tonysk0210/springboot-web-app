package com.company.MyWeb.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

//this profile class is used to store the form data and transfer it to the Person class. Entity not specified

/**
 * Profile class is designed to temporarily store Person information for display and validation during the information update
 * Since it won't be stored in the database, extending BaseEntity is not required
 */
@Data
public class Profile {
    @NotBlank(message = "⚠\uFE0F Name must not be blank")
    private String name;

    @NotBlank(message = "⚠\uFE0F Mobile number is required")
    @Pattern(regexp = "[0-9]{10}", message = "⚠\uFE0F Mobile Number must be exactly 10 digits long")
    private String mobile;

    @NotBlank(message = "⚠\uFE0F Email is required")
    private String email;

    @NotBlank(message = "⚠\uFE0F Address1 is required")
    private String address1;

    private String address2;

    @NotBlank(message = "⚠\uFE0F City is required")
    private String city;

    @NotBlank(message = "⚠\uFE0F Zip code is required")
    @Pattern(regexp = "[0-9]{3}", message = "⚠\uFE0F Zip code must be exactly 3 digits")
    private String zipcode;
}
