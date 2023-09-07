package com.gora.backend.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gora.backend.model.entity.TokenEntity;
import com.gora.backend.model.entity.eTokenUseDBType;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    Optional<TokenEntity> findByAccess(String accessToken);
    Optional<TokenEntity> findByAccessAndTypeAndAccessExpireAtAfter(String accessToken, eTokenUseDBType type, Date accessExpireAt);
    void deleteByAccess(String accessToken);
}
