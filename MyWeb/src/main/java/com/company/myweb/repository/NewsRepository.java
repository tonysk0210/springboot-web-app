package com.company.myweb.repository;

import com.company.myweb.model.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * News 的 Spring JDBC API 實作 — 唯一不走 JPA 的 repository（示範替代方案）
 *
 * JdbcTemplate：Spring 對 JDBC 的薄封裝，執行 SQL 並自動處理 ResultSet
 * BeanPropertyRowMapper：透過反射把 SQL 欄位對映到 POJO 的 setter（snake_case ↔ camelCase 自動轉換）
 *   → News 必須有 no-arg constructor + public setter（此處靠 Lombok 的 @NoArgsConstructor + @Data 提供）
 */
@Repository
public class NewsRepository {

    private final JdbcTemplate jdbcTemplate;  // 由 spring-boot-starter-jdbc 自動配置（依賴 application.properties 內的 datasource）

    @Autowired
    public NewsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<News> returnAListOfAllNewsItems() {
        String sql = "SELECT * FROM news";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(News.class));
    }

    /*
     * BeanPropertyRowMapper 欄位對映規則：
     *   SQL: released_date  →  POJO: releasedDate（snake_case 自動轉 camelCase）
     *
     *   | 情境                     | 結果                              |
     *   |--------------------------|-----------------------------------|
     *   | SQL 有欄位、POJO 沒欄位   | 忽略該欄位                        |
     *   | POJO 有欄位、SQL 沒欄位   | POJO 欄位保持預設值（null / 0）   |
     *   | 名稱不對應                | 不拋例外，僅不做對映              |
     */
}
