package com.gora.backend.repository;

import com.gora.backend.model.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenEntity, String> {
    Optional<TokenEntity> findByAccess(String accessToken);
}
