package com.gora.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "privilege")
@Getter
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class PrivilegeEntity extends DefaultColumn{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;
    @Column(name = "display_name")
    private String displayName;
    @Column
    private String code;
}
