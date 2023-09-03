package com.gora.backend.model.entity;

import java.util.Date;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "email_verify")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerifyEntity extends DefaultColumn{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    @OneToOne
    @Cascade(CascadeType.ALL)
    private TokenEntity token;
    @Column
    @Setter
    private Date verifiedExpireAt;
    @Column
    private String email;

    public static EmailVerifyEntity create(TokenEntity token, String email, Date verifiedExpireAt) {
        return EmailVerifyEntity.builder()
                .email(email)
                .verifiedExpireAt(verifiedExpireAt)
                .token(token)
                .build();
    }
}
