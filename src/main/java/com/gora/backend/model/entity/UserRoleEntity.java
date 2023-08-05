package com.gora.backend.model.entity;

import com.gora.backend.model.entity.DefaultColumn;
import com.gora.backend.model.entity.id.UserRoleId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_role")
@Getter
@IdClass(UserRoleId.class)
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserRoleEntity {
    @Id
    @Column(name = "user_seq")
    private long userSeq;
    @Id
    @Column(name = "role_seq")
    private long roleSeq;
}
