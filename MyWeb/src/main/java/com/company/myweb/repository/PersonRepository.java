package com.company.myweb.repository;

import com.company.myweb.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Person 的 Spring Data JPA repository。
 * 全部方法皆為 derived query — 名稱符合 Spring Data 命名慣例，實作由框架自動生成
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    Person readByEmail(String email);                              // SELECT ... WHERE email = ?

    boolean existsByEmail(String email);                           // 檢查該 email 是否已註冊

    boolean existsByEmailAndPersonIdNot(String email, int personId); // 檢查 email 存在但排除某 personId（更新自己資料時判重用）

    List<Person> findAllByOrderByPersonIdAsc();                    // 依 personId 升冪排序
}
