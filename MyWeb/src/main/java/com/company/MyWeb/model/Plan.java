package com.company.MyWeb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * 方案 Entity — 對應 DB 表 `plan`（schema.sql）
 * 與 Person 為一對多（雙向）：Person 持有外鍵 plan_id
 * owning side 在 Person.plan，此類為 mappedBy 反向端（僅供反查「這方案有哪些使用者」）
 */
@Getter
@Setter
@Entity
public class Plan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int planId;

    @NotBlank(message = "⚠\uFE0F 方案名稱不可為空")
    private String name;

    // 反向端（inverse side）：mappedBy 指向 Person.plan
    //   - owning side（Person.plan）負責寫入 person 表的 plan_id 欄位
    //   - 這裡的 persons 只是 read-only 視角，改動它（例如 plan.getPersons().add(p)）不會被 JPA 寫進 DB
    // FetchType.LAZY：查 Plan 時不預先撈使用者清單，避免載入大量 Person 造成效能問題
    // 未初始化：Set 為 null；建議改成 = new HashSet<>() 更安全，避免呼叫端 NPE
    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY)
    private Set<Person> persons = new HashSet<>();
}
