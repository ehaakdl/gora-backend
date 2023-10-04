package com.gora.backend.common.token;

import java.security.Key;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.gora.backend.model.TokenInfoDto;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenUtils {
    //    32글자 필요
    @Value("${app.secret-key:a89e2da3-704d-4ff0-a803-c8d8dc57cbf1}")
    private String SECRET_KEY;

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String getAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(authorization) && authorization.startsWith("Bearer ")) {
            return authorization;
        }
        return null;
    }

    public String getValue(String token, String claimName){
        try {
            return Jwts.parser()
                    .setSigningKey(getSecretKey())
                    .parseClaimsJws(token)
                    .getBody().get(claimName, String.class);
        }catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | ExpiredJwtException e){
            return null;
        }
    }

    private String createToken(Map<String, Object> claimsMap, String subject, Date expireAt) {

        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claimsMap)
                .setIssuedAt(new Date())
                .setExpiration(expireAt)
                .signWith(getSecretKey())
                .compact();
    }

    public TokenInfoDto createToken(Map<String, Object> claimsMap, eTokenType type){
        Date nowAt = new Date();
        Date expiredAt = new Date(nowAt.getTime() + type.getExpirePeriod());
        String token = createToken(claimsMap, type.getSubject(), expiredAt);
        return new TokenInfoDto("Bearer "+ token, expiredAt);
    }
}
