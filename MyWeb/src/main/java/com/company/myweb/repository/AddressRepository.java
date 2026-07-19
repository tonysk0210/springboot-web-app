package com.company.myweb.repository;

import com.company.myweb.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Address 的 Spring Data JPA repository。
 * JpaRepository 內建 CRUD、分頁、排序方法，Spring 執行期自動生成實作。
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
}
