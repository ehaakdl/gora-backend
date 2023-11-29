package com.gora.backend.model;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// todo db에서 관리
@Getter
@RequiredArgsConstructor
public enum eIgnoreSecurityPath {
    ICON(AntPathRequestMatcher.antMatcher("/*.ico"))
    , ERROR(AntPathRequestMatcher.antMatcher("/error"))
    , OAUTH2(AntPathRequestMatcher.antMatcher("/oauth2/**"))
    , SWAGGER_PREFIX(AntPathRequestMatcher.antMatcher("/swagger-ui/**"))
    , SWAGGER_DOCS(AntPathRequestMatcher.antMatcher("/docs"))
    , SWAGGER_DOCS_PREFIX(AntPathRequestMatcher.antMatcher("/docs/**"))
    , SWAGGER(AntPathRequestMatcher.antMatcher("/swagger-ui"))
    , LOGOUT(AntPathRequestMatcher.antMatcher("/api/v1/logout"))
    , LOGIN(AntPathRequestMatcher.antMatcher("/api/v1/login"))
    , SIGNUP(AntPathRequestMatcher.antMatcher("/api/v1/signup"))
    , EMAIL_VERIFY_SEND(AntPathRequestMatcher.antMatcher("/api/v1/user/email/verify-send"))
    , EMAIL_VERIFY(AntPathRequestMatcher.antMatcher("/api/v1/user/email-verify"))
    , USER_OAUTH_LOGIN_TOKEN(AntPathRequestMatcher.antMatcher("/api/v1/user/auth/oauth/login-token"))
    ;

    private final AntPathRequestMatcher requestMatcher;

    public static AntPathRequestMatcher[] getAntRequestMatchers() {
        eIgnoreSecurityPath[] ignoreSecurityPaths = values();
        AntPathRequestMatcher[] resultList = new AntPathRequestMatcher[ignoreSecurityPaths.length];

        for (int i = 0; i < ignoreSecurityPaths.length; i++) {
            eIgnoreSecurityPath ignoreSecurityPath = ignoreSecurityPaths[i];
            resultList[i] = ignoreSecurityPath.requestMatcher;
        }

        return resultList;
    }
}
