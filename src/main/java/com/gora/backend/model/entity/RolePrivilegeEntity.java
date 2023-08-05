package com.gora.backend.model.entity;

import com.gora.backend.model.entity.id.RolePrivilegeId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "role_privilege")
@Getter
@IdClass(RolePrivilegeId.class)
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RolePrivilegeEntity {
    @Id
    @Column
    private long roleSeq;
    @Id
    @Column
    private long privilegeSeq;
    @CreationTimestamp
    @Column
    private Date createdAt;
    @UpdateTimestamp
    @Column
    private Date updatedAt;
    @Column
    private Long createdBy;
    @Column
    private Long updatedBy;
}
