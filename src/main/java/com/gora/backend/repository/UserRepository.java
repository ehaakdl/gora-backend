package com.gora.backend.repository;

import com.gora.backend.model.entity.UserEntity;
import com.gora.backend.model.entity.eUserType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailAndType(String email, eUserType userType);
    Optional<UserEntity> findByEmail(String email);
    
}
