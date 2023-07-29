package com.gora.backend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "privilege")
@Getter
public class PrivilegeEntity extends DefaultColumn{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;
    @Column
    private String name;
    @Column(name = "display_name")
    private String displayName;
}
