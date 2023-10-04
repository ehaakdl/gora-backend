package com.gora.backend.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenInfoDto {
    private String token;
    private Date expiredAt;

}
