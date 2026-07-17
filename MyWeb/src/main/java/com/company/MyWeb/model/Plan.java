package com.company.MyWeb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

/**
 * Bidirectional to Person
 */
@Data
@Entity
public class Plan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int planId;

    @NotBlank(message = "⚠\uFE0F Plan name is required")
    private String name;

    // the inverse side
    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY, targetEntity = Person.class)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Person> persons; //? not initialized
}
