package com.gora.backend.service.security;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gora.backend.model.entity.SocialUserEntity;
import com.gora.backend.model.entity.TokenEntity;
import com.gora.backend.model.entity.UserEntity;
import com.gora.backend.model.entity.eSocialType;
import com.gora.backend.model.entity.eUserType;
import com.gora.backend.model.response.oauth2.GoogleUserProfile;
import com.gora.backend.repository.SocialUserRepository;
import com.gora.backend.repository.TokenRepository;
import com.gora.backend.repository.UserRepository;
import com.gora.backend.service.WebClientService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/*
* oauth2 서버에 access token으로 유저 정보 요청하는 곳*/
@RequiredArgsConstructor
public class Oauth2UserService extends DefaultOAuth2UserService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final SocialUserRepository socialUserRepository;
    private final WebClientService webClientService;
    private final ObjectMapper objectMapper;

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

        if (registrationId.equals("google")) {
            Object obj = sendGoogleUserProfileRequest(socialAccessToken);
            int aa = 1;
        } else {
            throw new RuntimeException("지원 안하는 소셜 로그인");
        }

        return CompletableFuture.completedFuture("dqwe");

    }

    private GoogleUserProfile sendGoogleUserProfileRequest(String accessToken) {
        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString("https://www.googleapis.com/oauth2/v1/userinfo")
                .queryParam("alt", "json").build();

        Mono<String> userInfoMonoResult = webClientService
                .sendGetRequest(t -> t.add(HttpHeaders.AUTHORIZATION, accessToken), uriComponents,
                        String.class);

        String userInfoTypeString = userInfoMonoResult.block(Duration.ofSeconds(2));
        try {
            return objectMapper.readValue(userInfoTypeString, GoogleUserProfile.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
