package com.gora.backend.model.entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "role")
@Getter
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class RoleEntity extends DefaultColumn{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;
    @Column
    private String code;
    @Column
    private String displayName;
    
    @Builder.Default
    @OneToMany(mappedBy = "role")
    private List<RolePrivilegeEntity> rolePrivilegeEntityEntities = new ArrayList<>();
}
