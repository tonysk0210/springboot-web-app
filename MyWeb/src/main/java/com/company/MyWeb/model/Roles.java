package com.company.MyWeb.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Unidirectional to Person
 */
@Data
@Entity
public class Roles extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roleId;

    private String roleName;
}
