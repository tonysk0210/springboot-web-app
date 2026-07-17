package com.company.MyWeb.repository;

import com.company.MyWeb.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Demonstrates Spring Data JPA application.
 * The implementation of ContactRepository interface will be created during runtime via Spring Data JPA
 * Map the POJO class and the primary key type @Id
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

    /**
     * Derived query method
     * A derived query method in JPA (Java Persistence API)—specifically when using Spring Data JPA—is a method in a repository interface
     * whose name follows a specific naming convention.
     * Spring Data JPA automatically generates the corresponding SQL (or JPQL) query based on this method name, without needing to write explicit query statements.
     * In Spring Data JPA, when you define a derived query method, you can choose the return type based on what best fits your use case.
     */
    List<Contact> findByStatus(String status); //JPA dervied method

    /**
     * This method is to return a Page<Contact> instance given the status and Pageable object
     * <p>
     * findByStatus - JPA derived method
     * findByStatusWithPageableAtQuery - JPQL @Query method (Entity-based JPQL query)
     * findByStatusWithPageNamed - JPQL @NamedQuery method (Entity-based JPQL query) - does not support dynamic sorting
     *
     * @Query(value = "SELECT * FROM contact_msg c WHERE c.status = :status", nativeQuery = true) -JPQL @QueryNative (Database-based JPQL Native query) - does not support dynamic sorting not supports pagination directly
     * <p>
     * JPA is smart enough to handle Pageable even not specified in the @Query and @NamedQuery
     */
    Page<Contact> readByStatus(String status, Pageable pageable); //JPA derived method - findByStatus clashing with Spring Data Rest

    @Query("SELECT c FROM Contact c WHERE c.status = :status")
    Page<Contact> findByStatusWithPageableAtQuery(@Param("status") String status, Pageable pageable); //@Param("status") can be omitted if the parameter matches the query parameter

    // @Query(name = "Contact.findByStatusWithPageNamed") is optional if method name matches, Dynamic sorting isnt working for @NamedQuery
    @Query(name = "Contact.findByStatusWithPageableNamed")
    Page<Contact> findByStatusWithPageableNamed(String status, Pageable pageable);

    /**
     * Demonstrates JPQL language with a customized query, which is part of JPA (Java Persistence API)
     * JPQL (Java Persistence Query Language) targets entity classes
     * The JPA provider (like Hibernate) translates JPQL into native SQL queries for the database.
     * Auditing fields (updatedAt, updatedBy) won’t be automatically updated by JPA auditing, bypassing EntityManager, which handles persistence operations:
     */
    @Transactional //Ensures the query runs within a transaction. Commit after the method ends, otherwise, fall back. | throws javax.persistence.TransactionRequiredException: Executing an update/delete query if not annotated.
    @Modifying //Tells Spring Data JPA this is a data-modifying query (not a select). MANDATORY
    @Query("UPDATE Contact c SET c.status = ?1, c.updatedAt = CURRENT_TIMESTAMP, c.updatedBy = ?2 WHERE c.contactId = ?3")
    int updateStatusById(String status, String updatedBy, int id); //[role]
    /*Since JPQL wont work along with AuditorAware, we have to manually update BaseEntity fields , the method name is flexible unlike JPA*/
}
