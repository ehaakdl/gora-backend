package com.gora.backend.model.entity;

import com.gora.backend.model.entity.id.UserRoleId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
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
    // todo 두개 모두 manytoone 달아놓기 기본키컬럼 따로 생성하기
    @Id
    @Column(name = "user_seq")
    private long userSeq;
    @Id
    @Column(name = "role_seq")
    // @ManyToOne
    // @JoinColumn(name = "role_seq")
    private RoleEntity role;
}
