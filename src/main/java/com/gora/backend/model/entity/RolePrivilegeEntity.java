package com.gora.backend.model.entity;

import com.gora.backend.model.entity.id.RolePrivilegeId;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "role_privilege")
@Getter
@IdClass(RolePrivilegeId.class)
public class RolePrivilegeEntity {
    @Id
    @Column
    private long roleSeq;
    @Id
    @Column
    private long privilegeSeq;
}
