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
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;
    @Column
    private long userSeq;
    @Column
    private String refresh;
    @Column
    private Date accessExpireAt;
    @Column
    private String access;

    public static TokenEntity createAccessToken(Long userSeq,String access, String refresh, Date accessExpireAt){
        if(StringUtils.isAnyBlank(access, refresh) || accessExpireAt == null){
            throw new IllegalArgumentException();
        }

        return TokenEntity.builder()
                .access(access)
                .refresh(refresh)
                .accessExpireAt(accessExpireAt)
                .userSeq(userSeq)
                .build();
    }
}
