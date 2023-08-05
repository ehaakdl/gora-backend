package com.gora.backend.model.entity;

import com.gora.backend.model.entity.DefaultColumn;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.*;

@Entity
@Table(name = "user")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends DefaultColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private Boolean disable;
    @Column
    @Enumerated(EnumType.STRING)
    private eUserType type;

    public boolean isDisable() {
        return Objects.requireNonNullElse(this.disable, false);
    }
}
