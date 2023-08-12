package com.gora.backend.common.token;

import com.gora.backend.model.TokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TokenUtils {
//    32글자 필요
    private static final String SECRET_KEY = "a89e2da3-704d-4ff0-a803-c8d8dc57cbf1";
    private final Environment environment;

    private Key getSecretKey() {
        String key = SECRET_KEY;
        return Keys.hmacShaKeyFor(key.getBytes());
    }

    public String parseLoginAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isNotBlank(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.replace("Bearer ","");
        }
        return null;
    }

    public String getValue(String token, String claimName){
        try {
            return Jwts.parser()
                    .parseClaimsJwt(token)
                    .getBody().get(claimName, String.class);
        }catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e){
            return null;
        }
    }

    private String createToken(Map<String, Object> claimsMap, String subject, Date expireAt) {
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claimsMap)
                .setIssuedAt(new Date())
                .setExpiration(expireAt)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public TokenInfo createToken(Map<String, Object> claimsMap, eToken type){
        Date nowAt = new Date();
        Date expiredAt = new Date(nowAt.getTime() + type.getExpirePeriod());
        return new TokenInfo(createToken(claimsMap, type.getSubject(), expiredAt), expiredAt);
    }
}