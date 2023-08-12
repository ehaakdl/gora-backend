package com.gora.backend.handler;

import com.gora.backend.common.ClaimsName;
import com.gora.backend.common.EnvironmentKey;
import com.gora.backend.model.entity.TokenEntity;
import com.gora.backend.repository.TokenRepository;
import com.gora.backend.common.token.TokenUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.gora.backend.common.token.eToken.ACCESS;
import static com.gora.backend.common.token.eToken.REFRESH;

@Service
@RequiredArgsConstructor
public class LoginSuccessHandler {
    private final TokenUtils tokenUtils;
    private final TokenRepository tokenRepository;
    private final Environment environment;

    public void process(HttpServletResponse response, Authentication authentication) {
        Date accessTokenExpireAt = new Date(System.currentTimeMillis() + ACCESS.getExpirePeriod());
        Date refreshTokenExpireAt = new Date(System.currentTimeMillis() + REFRESH.getExpirePeriod());

        String email = getEmail(authentication);
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(ClaimsName.EMAIL, email);

        String accessToken = tokenUtils.createToken(claimsMap, ACCESS, accessTokenExpireAt);
        String refreshToken = tokenUtils.createToken(claimsMap, REFRESH, refreshTokenExpireAt);
        tokenRepository.save(TokenEntity.createAccessToken(1L,accessToken, refreshToken, accessTokenExpireAt));

        String frontUrl = environment.getProperty(EnvironmentKey.APP_FRONT_URL);
        response.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        try {
            response.sendRedirect(String.format("%s/?access=%s", frontUrl, accessToken));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getEmail(Authentication authentication) {
        try {
            return (String) authentication.getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }
}
