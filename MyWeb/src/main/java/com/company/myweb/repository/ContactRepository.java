package com.company.myweb.repository;

import com.company.myweb.model.Contact;
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
 * Contact 的 Spring Data JPA repository — 教學用：示範四種查詢寫法對照
 *   1. Derived query（名稱慣例）
 *   2. @Query 自訂 JPQL
 *   3. @NamedQuery（宣告在 Entity 上）
 *   4. @Modifying + @Query 做 UPDATE / DELETE
 *
 * 前三個查同一件事（依 status 分頁），只是三種語法；第四個用來改狀態
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

    // ─── 1) Derived query：Spring 依方法名自動生成 SQL ───
    List<Contact> findByStatus(String status);

    // 分頁版：多加 Pageable 參數 Spring 自動處理 LIMIT/OFFSET
    // 注意方法名用 readByStatus 而非 findByStatus，避開 Spring Data REST 端點衝突
    Page<Contact> readByStatus(String status, Pageable pageable);

    // ─── 2) @Query JPQL 自訂查詢 ───
    // @Param 名稱與 :status 對應；若參數名一致（Java 8+ 編譯保留參數名）可省略
    @Query("SELECT c FROM Contact c WHERE c.status = :status")
    Page<Contact> findByStatusWithPageableAtQuery(@Param("status") String status, Pageable pageable);

    // ─── 3) @NamedQuery（實際定義在 Contact Entity 的 @NamedQueries 內） ───
    // name 對應 Entity 上宣告的 @NamedQuery 名稱；若方法名剛好等於「EntityName.methodName」也可省略
    @Query(name = "Contact.findByStatusWithPageableNamed")
    Page<Contact> findByStatusWithPageableNamed(String status, Pageable pageable);

    // ─── 4) @Modifying + @Query 做 UPDATE ───
    // @Transactional：UPDATE / DELETE 必須在 transaction 內執行，否則拋 TransactionRequiredException
    // @Modifying：告訴 Spring Data JPA 這是「寫入」型查詢，不是 SELECT
    // 注意：@Modifying JPQL 繞過 JPA lifecycle → AuditingEntityListener 不觸發
    //       所以要「手動」把 updatedAt、updatedBy 寫進 SQL（無法依賴 @LastModifiedBy 自動填）
    @Transactional
    @Modifying
    @Query("UPDATE Contact c SET c.status = ?1, c.updatedAt = CURRENT_TIMESTAMP, c.updatedBy = ?2 WHERE c.contactId = ?3")
    int updateStatusById(String status, String updatedBy, int id);
}
