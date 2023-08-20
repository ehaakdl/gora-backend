package com.gora.backend.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gora.backend.common.ClaimsName;
import com.gora.backend.common.EnvironmentKey;
import com.gora.backend.common.token.TokenUtils;
import com.gora.backend.model.TokenInfo;
import com.gora.backend.model.entity.TokenEntity;
import com.gora.backend.model.entity.UserEntity;
import com.gora.backend.model.entity.UserRoleEntity;
import com.gora.backend.model.entity.eUserType;
import com.gora.backend.repository.RoleRepository;
import com.gora.backend.repository.TokenRepository;
import com.gora.backend.repository.UserRepository;
import com.gora.backend.repository.UserRoleRepository;

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

    private UserEntity getBasicUser(String email,String password, eUserType userType) {
        UserEntity user = userRepository.findByEmailAndDisable(email, false).orElse(null);
        return Objects.requireNonNullElseGet(user, () ->  userRepository.save(
                        UserEntity.builder()
                                .type(userType)
                                .password(password)
                                .email(email)
                                .build()
                )
        );
    }

    private UserEntity getSocialUser(String email, eUserType userType) {
        UserEntity user = userRepository.findByEmailAndDisable(email, false).orElse(null);
        return Objects.requireNonNullElseGet(user, () ->
                 userRepository.save(
                        UserEntity.builder()
                                .type(userType)
                                .email(email)
                                .build()
                 )
        );
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

    private String getPassword(Authentication authentication){
//        todo basic 계정 임시 비번
        return passwordEncoder.encode("1234");
    }

    @Transactional
    public void process(HttpServletResponse response, Authentication authentication) {
        String email = extractEmail(authentication);
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(ClaimsName.EMAIL, email);

        eUserType userType = getUserType(authentication);
        UserEntity user;
        if(userType == eUserType.basic){
            String password = getPassword(authentication);
            user = getBasicUser(email, password, userType);
        }else {
            user = getSocialUser(email,userType);
        }

        // todo 아이디 생성시 기본권한도 부여하기
        roleRepository.findByCode("R_PUBLIC").ifPresent(t -> {
            userRoleRepository.save(
            UserRoleEntity.builder().roleSeq()
        );
        });
        

        TokenInfo accessTokenInfo = tokenUtils.createToken(claimsMap, ACCESS);
        TokenInfo refreshTokenInfo = tokenUtils.createToken(claimsMap, REFRESH);
        tokenRepository.save(
                TokenEntity.createAccessToken(
                        user, accessTokenInfo.getToken(), refreshTokenInfo.getToken()
                        , accessTokenInfo.getExpiredAt()
                )
        );

        setResponse(response, accessTokenInfo.getToken());
    }

}
