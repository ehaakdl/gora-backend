package com.gora.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gora.backend.model.entity.SocialUserEntity;

public interface SocialUserRepository extends JpaRepository<SocialUserEntity, Long> {
}
