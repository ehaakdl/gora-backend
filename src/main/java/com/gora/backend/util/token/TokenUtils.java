package com.gora.backend.util.token;

import com.gora.backend.constant.EnvironmentKey;
import com.gora.backend.model.LoginTokenPair;
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
    private static final String SECRET_KEY = "1234";
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

    public String createToken(Map<String, Object> claimsMap, eToken type, Date expireAt) {
        return Jwts.builder()
                .setSubject(type.getSubject())
                .addClaims(claimsMap)
                .setIssuedAt(new Date())
                .setExpiration(expireAt)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
