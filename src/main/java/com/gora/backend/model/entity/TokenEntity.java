package com.gora.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

@Entity
@Table(name = "token")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenEntity extends DefaultColumn{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;
    @Column(name = "user_seq")
    private long userSeq;
    @Column
    private String refresh;
    @Column(name = "access_expire_at")
    private Date accessExpireAt;
    @Column(name = "access")
    private String access;

    public static TokenEntity createAccessToken(String access, String refresh, Date accessExpireAt){
        if(StringUtils.isAnyBlank(access, refresh) || accessExpireAt == null){
            throw new IllegalArgumentException();
        }

        return TokenEntity.builder()
                .access(access)
                .refresh(refresh)
                .accessExpireAt(accessExpireAt)
                .build();
    }
}
