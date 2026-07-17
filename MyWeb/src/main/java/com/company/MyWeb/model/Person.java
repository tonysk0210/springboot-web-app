package com.company.MyWeb.model;

import com.company.MyWeb.myValidation.FieldValueMatchValidator;
import com.company.MyWeb.myValidation.PasswordValidator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
/*Custom validator - The List container annotation holds two @FieldValueMatchValidator constraints.*/
@FieldValueMatchValidator.List({
        @FieldValueMatchValidator(field = "password", fieldMatch = "confirmPassword", message = "⚠\uFE0F Passwords do not match"),
        @FieldValueMatchValidator(field = "email", fieldMatch = "confirmEmail", message = "⚠\uFE0F Emails do not match")})
@ToString(callSuper = true)
public class Person extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int personId;

    @NotBlank(message = "⚠\uFE0F Name must not be blank")
    private String name;

    @Pattern(regexp = "[0-9]{10}", message = "⚠\uFE0F Mobile Number must be exactly 10 digits long")
    private String mobile;

    private String email;

    @Transient
    @JsonIgnore
    private String confirmEmail;

    @PasswordValidator
    @JsonIgnore
    private String password;

    @Transient
    @JsonIgnore
    private String confirmPassword;

    /* This is effectively “HIDING” the createdBy field that lived in BaseEntity so AuditorAware will ignore createdBy when performing INSERT operation
    for the first time when a new user tries to register without Authentication established*/
    private String createdBy;

    /*@JoinedColumn is used when the entity class owns the foreign key to the field*/

    // unidirectional to Roles
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Roles.class, optional = false)
    @JoinColumn(name = "role_id", referencedColumnName = "roleId", nullable = false)
    private Roles roles;

    // unidirectional to Address
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE}, targetEntity = Address.class)
    @JoinColumn(name = "address_id", referencedColumnName = "addressId", nullable = true)
    private Address address;

    // bidirectional with Plan
    @ManyToOne(fetch = FetchType.EAGER, optional = true) //JPA level
    @JoinColumn(name = "plan_id", referencedColumnName = "planId", nullable = true) //SQL level
    private Plan plan;

    // bidirectional with Courses
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "person_courses",
            joinColumns = @JoinColumn(name = "person_id", referencedColumnName = "personId"),
            inverseJoinColumns = @JoinColumn(name = "course_id", referencedColumnName = "courseId"))
    private Set<Courses> courses = new HashSet<>();

}
