package com.gora.backend.model.entity.user;

import com.gora.backend.model.entity.DefaultColumn;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Table(name = "user")
@Getter
@Builder
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

    public boolean isDisable() {
        return Objects.requireNonNullElse(this.disable, false);
    }
}
