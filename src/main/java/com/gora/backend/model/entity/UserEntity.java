package com.gora.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.usertype.UserType;

import java.util.*;

@Entity
@Table(name = "user")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class UserEntity extends DefaultColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    @Enumerated(EnumType.STRING)
    private eUserType type;
    
    public static UserEntity createSocialUser(eUserType type, String email){
        return UserEntity.builder()
                                .type(type)
                                .email(email)
                                .build();
    }
    
    public static UserEntity createBasicUser(eUserType type, String password, String email){
        return UserEntity.builder()
                                .type(type)
                                .password(password)
                                .email(email)
                                .build();

    }
}
