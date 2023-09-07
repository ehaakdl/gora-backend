package com.gora.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gora.backend.model.entity.EmailVerifyEntity;

public interface EmailVerifyRepository extends JpaRepository<EmailVerifyEntity, Long> {
    List<EmailVerifyEntity> findAllByEmail(String email);
}
