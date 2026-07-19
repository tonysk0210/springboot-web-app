package com.company.myweb.repository;

import com.company.myweb.model.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Courses 的 Spring Data JPA repository。
 * 兩個方法都是 derived query 內建的靜態排序（OrderBy）。
 * 亦可透過 Spring Data REST 端點測試：/spring-data-api/courseses/search
 */
@Repository
public interface CoursesRepository extends JpaRepository<Courses, Integer> {

    List<Courses> findByOrderByNameDesc();  // 依 name 降冪

    List<Courses> findByOrderByName();      // 依 name 升冪（預設）
}
