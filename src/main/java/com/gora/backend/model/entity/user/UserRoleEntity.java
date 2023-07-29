package com.gora.backend.model.entity.user;

import com.gora.backend.model.entity.DefaultColumn;
import com.gora.backend.model.entity.id.UserRoleId;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "user_role")
@Getter
@IdClass(UserRoleId.class)
public class UserRoleEntity extends DefaultColumn {
    @Id
    @Column(name = "user_seq")
    private long userSeq;
    @Id
    @Column(name = "role_seq")
    private long roleSeq;
}
