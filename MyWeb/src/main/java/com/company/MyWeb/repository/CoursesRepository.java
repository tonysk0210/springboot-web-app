package com.company.MyWeb.repository;

import com.company.MyWeb.model.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoursesRepository extends JpaRepository<Courses, Integer> {
    /* Both are JPA derived query method: static sorting
    * http://localhost:8081/spring-data-api/courseses/search shows the methods supported by Spring Data Rest*/
    List<Courses> findByOrderByNameDesc();

    List<Courses> findByOrderByName();
}
