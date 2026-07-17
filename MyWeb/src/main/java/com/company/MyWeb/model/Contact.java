package com.company.MyWeb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)

@Entity //required for JPA
@Table(name = "contact_msg")

/* Implementation syntax of @NamedQuery */
@NamedQueries({
        @NamedQuery(name = "Contact.findByStatusWithPageableNamed", query = "SELECT c FROM Contact c WHERE c.status = :status")
})

public class Contact extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //This tells JPA to use the database’s identity mechanism without needing @GenericGenerator
    @Column(name = "contact_id")
//  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native") //tells JPA how to generate primary key values. strategy = GenerationType.AUTO: Let JPA provider(e.g., Hibernate) choose the best strategy on the underlying database. generator = "native" references a custom generator defined by @GenericGenerator
//  @GenericGenerator(name = "native", strategy = "native") //and letting Hibernate handle the generation. name = "native": defines a custom generator named "native". strategy = "native" tells Hibernate to: Use the database’s native identity generation (e.g., AUTO_INCREMENT for MySQL)
    private int contactId;

    @NotBlank(message = "⚠\uFE0FName must not be blank⚠\uFE0F")
    private String name;

    //using Bean Validation annotations to validate the Contact form fields
    @Pattern(regexp = "[0-9]{10}", message = "⚠\uFE0FMobile Number must be exactly 10 digits long⚠\uFE0F")
    private String mobile;

    private String email;

    @NotBlank(message = "⚠\uFE0FSubject must not be blank⚠\uFE0F")
    private String subject;

    @NotBlank(message = "⚠\uFE0FMessage must not be blank⚠\uFE0F")
    private String message;

    private String status;
}

