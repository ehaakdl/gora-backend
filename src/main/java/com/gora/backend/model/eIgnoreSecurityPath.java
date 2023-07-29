package com.gora.backend.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Getter
@RequiredArgsConstructor
public enum eIgnoreSecurityPath {
    ICON(AntPathRequestMatcher.antMatcher("/*.ico"))
    , ERROR(AntPathRequestMatcher.antMatcher("/error"))
    , OAUTH(AntPathRequestMatcher.antMatcher("/oauth2/**"))
    , LOGIN(AntPathRequestMatcher.antMatcher("/test"))
    , SWAGGER_PREFIX(AntPathRequestMatcher.antMatcher("/swagger-ui/**"))
    , SWAGGER_DOCS(AntPathRequestMatcher.antMatcher("/docs"))
    , SWAGGER_DOCS_PREFIX(AntPathRequestMatcher.antMatcher("/docs/**"))
    , SWAGGER(AntPathRequestMatcher.antMatcher("/swagger-ui"))
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
