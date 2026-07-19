package com.company.myweb.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)   // 明確告訴 Lombok「audit 欄位不參與 equality」，兩個 News 只要 newsId、title 等本身欄位一樣就算 equal，createdAt 不同也視為相等。
@ToString(callSuper = true)
public class News extends BaseEntity {

    private int newsId;
    private String title;
    private LocalDate releasedDate;
    private String content;

}
