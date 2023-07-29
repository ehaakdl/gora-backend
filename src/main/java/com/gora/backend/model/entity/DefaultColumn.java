package com.gora.backend.model.entity;

import jakarta.persistence.Column;

import java.util.Date;

public abstract class DefaultColumn {
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "updated_at")
    private Date updatedAt;
    @Column(name = "created_by")
    private Long createdBy;
    @Column(name = "updated_by")
    private Long updatedBy;
}
