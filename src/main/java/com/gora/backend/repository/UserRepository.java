package com.gora.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gora.backend.model.entity.UserEntity;
import com.gora.backend.model.entity.eUserType;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailAndType(String email, eUserType userType);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    
}
