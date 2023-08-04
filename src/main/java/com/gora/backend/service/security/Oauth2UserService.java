package com.gora.backend.service.security;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
/*
* oauth2 서버에 access token으로 유저 정보 요청하는 곳*/
public class Oauth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        boolean dd = false;
        System.out.printf("loaduser");
        if (dd) {
            throw new RuntimeException();
        }
        return super.loadUser(userRequest);
    }
}
