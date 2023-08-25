package com.gora.backend.model.entity;

import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_role")
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;

    @ManyToOne
    @JoinColumn(name = "user_seq")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "role_seq")
    private RoleEntity role;

    public static UserRoleEntity create(UserEntity user, RoleEntity role){
        return UserRoleEntity.builder().user(user).role(role).build();
    }
}
