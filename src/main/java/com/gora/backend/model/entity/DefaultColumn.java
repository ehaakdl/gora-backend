package com.gora.backend.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class DefaultColumn {
    @CreationTimestamp
    @Column
    private Date createdAt;
    @Column
    private Date deletedAt;
    @UpdateTimestamp
    @Column
    private Date updatedAt;
    @Column
    private Long createdBy;
    @Column
    private Long updatedBy;
    @Column
    private Long deletedBy;
}
