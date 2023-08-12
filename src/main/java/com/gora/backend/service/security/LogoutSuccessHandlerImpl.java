package com.gora.backend.service.security;

import com.gora.backend.common.CookieUtils;
import com.gora.backend.common.EnvironmentKey;
import com.gora.backend.common.HeaderName;
import com.gora.backend.repository.TokenRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    private final Environment environment;
    private final TokenRepository tokenRepository;

    @Override
    @Transactional
//    todo 트랜잭션 확인
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String accessToken = request.getHeader(HeaderName.ACCESS_TOKEN.name());
        if(accessToken == null){
            return;
        }

        tokenRepository.findByAccess(accessToken).ifPresent(tokenRepository::delete);
        String frontUrl = environment.getProperty(EnvironmentKey.APP_FRONT_URL);
        response.sendRedirect(frontUrl);
    }
}
