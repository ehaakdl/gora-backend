package com.gora.backend.repository;

import com.gora.backend.model.entity.RoleEntity;
import com.gora.backend.model.entity.UserEntity;
import com.gora.backend.model.entity.UserRoleEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    boolean existsByUserAndRole(UserEntity user, RoleEntity role);
    List<UserRoleEntity> findAllByUser(UserEntity user);
}
