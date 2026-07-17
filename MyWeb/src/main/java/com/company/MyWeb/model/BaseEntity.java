package com.company.MyWeb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * when calling repository.save(), it triggers auditing via AuditingEntityLister
 * then...
 * 1) createdAt and updatedAt are filled with LocalDateTime.now().
 * 2) createdBy and updatedBy are filled by the AuditorAware bean.
 */
@Data
@MappedSuperclass //JPA annotation - define common properties for subclasses, without creating separate table
@EntityListeners(AuditingEntityListener.class) //enable JPA auditing - entity level
public class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    @JsonIgnore
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(updatable = false)
    @JsonIgnore
    private String createdBy;

    @LastModifiedDate
    @Column(insertable = false)
    @JsonIgnore
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(insertable = false)
    @JsonIgnore
    private String updatedBy;
}


