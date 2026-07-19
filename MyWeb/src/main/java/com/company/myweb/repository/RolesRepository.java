package com.company.myweb.repository;

import com.company.myweb.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Roles 的 Spring Data JPA repository。
 * 注意：Spring Data JPA 的 get/find/read 三種前綴都是「查詢」意思，效果相同。
 */
@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer> {

    // Derived query：Spring 依方法名自動生成 SELECT ... WHERE role_name = ?
    Roles getByRoleName(String roleName);
}
