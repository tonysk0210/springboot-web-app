package com.company.MyWeb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "contact_msg")

/* @NamedQuery 語法示範 */
@NamedQueries({
        @NamedQuery(
                name = "Contact.findByStatusWithPageableNamed",
                query = "SELECT c FROM Contact c WHERE c.status = :status")
})

public class Contact extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private int contactId;

    @NotBlank(message = "⚠\uFE0F姓名不可為空⚠\uFE0F")
    private String name;

    // 使用 Bean Validation 註解驗證 Contact 表單欄位
    @Pattern(regexp = "[0-9]{10}", message = "⚠\uFE0F手機號碼必須為 10 位數字⚠\uFE0F")
    private String mobile;

    private String email;

    @NotBlank(message = "⚠\uFE0F主旨不可為空⚠\uFE0F")
    private String subject;

    @NotBlank(message = "⚠\uFE0F訊息內容不可為空⚠\uFE0F")
    private String message;

    private String status;
}

