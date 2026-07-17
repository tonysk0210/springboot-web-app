package com.company.MyWeb.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class News extends BaseEntity {

    private int newsId;
    private String title;
    private LocalDate releasedDate;
    private String content;

}
