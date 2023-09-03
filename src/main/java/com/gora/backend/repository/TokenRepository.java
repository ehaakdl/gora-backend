package com.gora.backend.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gora.backend.model.entity.TokenEntity;
import com.gora.backend.model.entity.eTokenUseType;

public interface TokenRepository extends JpaRepository<TokenEntity, String> {
    Optional<TokenEntity> findByAccess(String accessToken);
    Optional<TokenEntity> findByAccessAndTypeAndExpireAtAfter(String accessToken, eTokenUseType type, Date expireAt);
}
