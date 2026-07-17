package com.company.MyWeb.repository;

import com.company.MyWeb.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The prefixes get, find, and read are all interpreted as “find” operations by Spring Data.
 */
@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer> {

    Roles getByRoleName(String roleName); //JPA derived query method
}
