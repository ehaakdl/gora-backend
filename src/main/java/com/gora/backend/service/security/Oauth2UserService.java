package com.gora.backend.service.security;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
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

    // todo 여러 종류 소셜 로그인 지원 가능하게 만들기
    @Async
    public CompletableFuture<String> loadUser(String registrationId, String socialAccessToken, Date issuedAt,
            Date expiredAt, String redirectionUri) {
        Instant issuedAtTypeInstant = Instant.ofEpochMilli(issuedAt.getTime());
        Instant expiredAtTypeInstant = Instant.ofEpochMilli(expiredAt.getTime());
        String clientSecret;

        if (registrationId.equals("google")) {
            clientSecret = System.getenv("OAUTH_GOOGLE_CLIENT_SECRET");
        } else {
            throw new RuntimeException("지원 안하는 소셜 로그인");
        }

        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(TokenType.BEARER, socialAccessToken,
                issuedAtTypeInstant,
                expiredAtTypeInstant);

        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(registrationId)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientId(socialAccessToken)
                .clientSecret(clientSecret)
                .redirectUri(redirectionUri)
                .build();

        OAuth2UserRequest request = new OAuth2UserRequest(clientRegistration, oAuth2AccessToken);
        super.loadUser(request);

        return CompletableFuture.completedFuture("");

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
