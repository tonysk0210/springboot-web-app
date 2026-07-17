package com.company.MyWeb.repository;

import com.company.MyWeb.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    Person readByEmail(String email); //JPA derived query method

    boolean existsByEmail(String email); //JPA derived query method - Returns true if a Person with this email already exists

    boolean existsByEmailAndPersonIdNot(String email, int personId); //JPA derived query method

    List<Person> findAllByOrderByPersonIdAsc(); //JPA derived query SORTING method

}
