package com.gora.backend.model.entity;

import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "token")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq")
    private UserEntity user;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "email_verify_seq")
    private EmailVerifyEntity emailVerify;
    @Column
    private String refresh;
    @Column
    private Date accessExpireAt;
    @Column
    private String access;
    @Column
    @Enumerated(EnumType.STRING)
    private eTokenUseDBType type;

    public void setEmailVerify(EmailVerifyEntity emailVerify) {
        this.emailVerify = emailVerify;
    }

    public static TokenEntity createLoginToken(UserEntity user, String access, String refresh, Date accessExpireAt) {
        return TokenEntity.builder()
                .access(access)
                .refresh(refresh)
                .accessExpireAt(accessExpireAt)
                .user(user)
                .type(eTokenUseDBType.login)
                .build();
    }

    public static TokenEntity createEmailVerifyToken(EmailVerifyEntity emailVerify, String access, Date accessExpireAt) {
        TokenEntity tokenEntity = TokenEntity.builder()
                .access(access)
                .emailVerify(emailVerify)
                .accessExpireAt(accessExpireAt)
                .type(eTokenUseDBType.email_verify)
                .build();

        emailVerify.setToken(tokenEntity);

        return tokenEntity;
    }
}
