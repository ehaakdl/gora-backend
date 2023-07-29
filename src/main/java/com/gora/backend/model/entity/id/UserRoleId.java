package com.gora.backend.model.entity.id;

import jakarta.persistence.Column;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class UserRoleId implements Serializable {
    @Column(name = "role_seq")
    private long roleSeq;
    @Column(name = "user_seq")
    private long userSeq;
}