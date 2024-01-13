package com.gora.backend.service.security;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;

import com.gora.backend.model.entity.SocialUserEntity;
import com.gora.backend.model.entity.TokenEntity;
import com.gora.backend.model.entity.UserEntity;
import com.gora.backend.model.entity.eSocialType;
import com.gora.backend.model.entity.eUserType;
import com.gora.backend.repository.SocialUserRepository;
import com.gora.backend.repository.TokenRepository;
import com.gora.backend.repository.UserRepository;
import com.gora.backend.service.WebClientService;

import io.netty.util.concurrent.Future;
import lombok.RequiredArgsConstructor;

/*
* oauth2 서버에 access token으로 유저 정보 요청하는 곳*/
@RequiredArgsConstructor
public class Oauth2UserService extends DefaultOAuth2UserService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final SocialUserRepository socialUserRepository;
    private final WebClientService webClientService;

    private UserEntity updateSocialUser(String email) {
        UserEntity user = userRepository.findByEmailAndType(email, eUserType.social).orElse(null);
        return Objects.requireNonNullElseGet(user, () -> {
            UserEntity _user = userRepository.save(
                    UserEntity.createSocialUser(email));

            eSocialType socialType = eSocialType.convert(email);
            if (socialType == null) {
                throw new RuntimeException();
            }

            socialUserRepository
                    .save(SocialUserEntity.builder().updatedBy(-1L).updatedAt(new Date()).createdBy(-1L)
                            .createdAt(new Date()).socialType(socialType).build());
            return _user;
        });
    }

    // todo 비동기로 구글 계정 정보 가져오는 함수 만들기
    @Async
    public Future<String> loadUser(String socialAccessToken, Date issuedAt, Date expiredAt) {
        Instant ee = Instant.ofEpochMilli(new Date(System.currentTimeMillis() + 10000000).getTime());
    
        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(TokenType.BEARER, socialAccessToken, issuedAt,
                expiredAt);
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2AccessToken oauth2Token = userRequest.getAccessToken();
        String token = oauth2Token.getTokenValue();
        Date expiredAt = Date.from(oauth2Token.getExpiresAt());

        OAuth2User defaultOAuth2User = super.loadUser(userRequest);
        String email = defaultOAuth2User.getAttribute("email");
        UserEntity user = updateSocialUser(email);
        tokenRepository.save(TokenEntity.createOauthToken(token, expiredAt, user));

        return defaultOAuth2User;
    }
}
