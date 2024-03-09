package com.gora.backend.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gora.backend.common.EnvironmentKey;
import com.gora.backend.common.RoleCode;
import com.gora.backend.common.TokenClaimsName;
import com.gora.backend.common.token.TokenUtils;
import com.gora.backend.common.token.eTokenType;
import com.gora.backend.model.TokenInfoDto;
import com.gora.common.model.entity.TokenEntity;
import com.gora.common.model.entity.UserEntity;
import com.gora.common.model.entity.UserRoleEntity;
import com.gora.common.model.entity.eUserType;
import com.gora.common.repository.RoleRepository;
import com.gora.common.repository.TokenRepository;
import com.gora.common.repository.UserRepository;
import com.gora.common.repository.UserRoleRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginSuccessHandler {
    private final TokenUtils tokenUtils;
    private final TokenRepository tokenRepository;
    private final Environment environment;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    private void setResponse(HttpServletResponse response, String accessToken) {
        String frontUrl = environment.getProperty(EnvironmentKey.APP_FRONT_URL);
        response.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        try {
            response.sendRedirect(String.format("%s/?access=%s", frontUrl, accessToken));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private UserEntity getBasicUser(String email, String password) {
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        return Objects.requireNonNullElseGet(user, () -> userRepository.save(
                UserEntity.createBasicUser(password, email)));
    }

    private UserEntity getSocialUser(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    private eUserType getUserType(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            return eUserType.social;
        } else {
            return eUserType.basic;
        }
    }

    private String extractEmail(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            DefaultOAuth2User user = (DefaultOAuth2User) ((OAuth2AuthenticationToken) authentication).getPrincipal();
            return user.getAttribute("email");
        } else {
            return (String) authentication.getPrincipal();
        }
    }

    private String getPassword(Authentication authentication) {
        // todo basic 계정 임시 비번
        return passwordEncoder.encode("1234");
    }

    @Transactional
    public void process(HttpServletResponse response, Authentication authentication) {
        // 인증 요청에서 이메일 추출
        String email = extractEmail(authentication);
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(TokenClaimsName.EMAIL, email);

        // 계정 가져오기
        eUserType userType = getUserType(authentication);
        UserEntity user;
        if (userType == eUserType.basic) {
            String password = getPassword(authentication);
            user = getBasicUser(email, password);
        } else {
            user = getSocialUser(email);
            if (user == null) {
                throw new RuntimeException();
            }
        }

        roleRepository.findByCode(RoleCode.ROLE_PUBLIC).ifPresentOrElse((role) -> {
            // 계정에 권한 없으면 부여
            if (!userRoleRepository.existsByUserAndRole(user, role)) {
                userRoleRepository.save(UserRoleEntity.create(user, role));
            }
        }, () -> {
            throw new RuntimeException("일반 유저 권한 지정 불가");
        });

        TokenInfoDto accessTokenInfo = tokenUtils.createToken(claimsMap, eTokenType.ACCESS);
        TokenInfoDto refreshTokenInfo = tokenUtils.createToken(claimsMap, eTokenType.REFRESH);
        tokenRepository.save(
                TokenEntity.createLoginToken(
                        user, accessTokenInfo.getToken(), refreshTokenInfo.getToken(), accessTokenInfo.getExpiredAt()));

        setResponse(response, accessTokenInfo.getToken());
    }

}
