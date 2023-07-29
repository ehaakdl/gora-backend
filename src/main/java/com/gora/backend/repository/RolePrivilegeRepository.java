package com.gora.backend.repository;

import com.gora.backend.model.entity.RolePrivilegeEntity;
import com.gora.backend.model.entity.id.RolePrivilegeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePrivilegeRepository extends JpaRepository<RolePrivilegeEntity, RolePrivilegeId> {
}
