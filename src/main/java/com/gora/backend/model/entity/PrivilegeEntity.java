package com.gora.backend.model.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    @Builder.Default
    @OneToMany(mappedBy = "privilege")
    private List<RolePrivilegeEntity> rolePrivilegeEntities = new ArrayList<>();
}
