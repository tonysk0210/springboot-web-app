package com.company.myweb.repository;

import com.company.myweb.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Plan 的 Spring Data JPA repository（純繼承 JpaRepository，無自訂查詢）
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {
}
