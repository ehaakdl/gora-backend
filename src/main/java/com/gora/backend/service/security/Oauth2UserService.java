package com.gora.backend.service.security;

import java.time.Duration;
import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gora.backend.common.ResponseCode;
import com.gora.backend.exception.BadRequestException;
import com.gora.backend.model.response.oauth2.GoogleUserProfile;
import com.gora.backend.service.WebClientService;
import com.gora.backend.service.user.UserService;
import com.gora.common.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/*
* oauth2 서버에 access token으로 유저 정보 요청하는 곳*/
@RequiredArgsConstructor
public class Oauth2UserService extends DefaultOAuth2UserService {
    private final UserService userService;
    private final WebClientService webClientService;
    private final ObjectMapper objectMapper;

    // todo 여러 종류 소셜 로그인 지원 가능하게 만들기, 비동기 코드로 작성
    public GoogleUserProfile loadUser(String registrationId, String socialAccessToken) {

        if (registrationId.equals("google")) {
            return sendGoogleUserProfileRequest("Bearer" + socialAccessToken);
        } else {
            throw new RuntimeException("지원 안하는 소셜 로그인");
        }
    }

    private GoogleUserProfile sendGoogleUserProfileRequest(String accessToken) {
        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString("https://www.googleapis.com/oauth2/v1/userinfo")
                .queryParam("alt", "json").build();
        Mono<String> userInfoMonoResult = webClientService
                .sendGetRequest(t -> t.add(HttpHeaders.AUTHORIZATION, accessToken), uriComponents,
                        String.class);
        // webflux 에러 처리 방법 연구하기, 코드가 비효율적임
        String userInfoTypeString;
        try {
            userInfoTypeString = userInfoMonoResult.block(Duration.ofSeconds(2));
        } catch (Exception e) {
            throw new BadRequestException(ResponseCode.BAD_REQUEST, "error.expiredToken");
        }

        try {
            return objectMapper.readValue(userInfoTypeString.getBytes(), GoogleUserProfile.class);
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
        UserEntity user = userService.upsertSocialUser(email);
        userService.upsertOauth2Token(user, token, expiredAt);

        return defaultOAuth2User;
    }
}
