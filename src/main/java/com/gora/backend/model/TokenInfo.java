package com.gora.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class TokenInfo {
    private String token;
    private Date expiredAt;

}
