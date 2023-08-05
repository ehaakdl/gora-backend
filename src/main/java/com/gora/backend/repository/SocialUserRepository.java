package com.gora.backend.repository;

import com.gora.backend.model.entity.PrivilegeEntity;
import com.gora.backend.model.entity.SocialUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialUserRepository extends JpaRepository<SocialUserEntity, Long> {
}
