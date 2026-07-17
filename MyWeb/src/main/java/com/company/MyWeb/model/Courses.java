package com.company.MyWeb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Bidirectional to Person
 */
@Data
@Entity
public class Courses extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int courseId;

    @NotBlank(message = "⚠\uFE0F Course name is required")
    private String name;

    @NotBlank(message = "⚠\uFE0F Course fees is required")
    @Pattern(regexp = "[0-9]+", message = "⚠\uFE0F Numeric digits only")
    private String fees;

    // the inverse side
    @ManyToMany(mappedBy = "courses", fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Person> persons = new HashSet<>();

}
