package com.company.myweb.repository;

import com.company.myweb.model.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring JDBC API
 */
@Repository
public class NewsRepository {

    private final JdbcTemplate jdbcTemplate; //created via application.properties setup

    @Autowired
    public NewsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<News> returnAListOfAllNewsItems() {
        String sql = "SELECT * FROM news";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(News.class)); //using BeanPropertyRowMappers
    }

    /*
    BeanPropertyRowMapper.newInstance(News.class): Creates a RowMapper that automatically maps column names to Java bean properties.

    News class must have:
    1) Public getters and setters for each field.
    2) Field names (or Java property names) matching the SQL column names (case -insensitive).

    SQL->POJO field
    released_date -> releasedDate (convention)
    released_date -> released_date
    released_date -> releaseddate

    | Scenario                                     | Result                                     |
    | -------------------------------------------- | ------------------------------------------ |
    | **SQL has extra columns, POJO lacks fields** | Extra columns ignored.                     |
    | **POJO has extra fields, SQL lacks columns** | Fields retain default values (`null`/`0`). |
    | **Mismatch doesn’t cause exceptions**        | Mapping only applies to matching names.    |
    */
}
