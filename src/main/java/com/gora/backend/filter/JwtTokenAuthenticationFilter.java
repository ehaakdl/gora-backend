package com.gora.backend.filter;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gora.backend.common.token.TokenUtils;
import com.gora.backend.service.security.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final RequestMatcher[] ignoreSecurityRequestMatchers;
    private final TokenUtils tokenUtils;

    private boolean ignoreSecurityPath(HttpServletRequest request) {
        for (RequestMatcher requestMatcher : ignoreSecurityRequestMatchers) {
            if (requestMatcher.matcher(request).isMatch()){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (ignoreSecurityPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = tokenUtils.getAccessToken(request);
        if(StringUtils.isBlank(accessToken)){
                response.sendError(HttpStatus.UNAUTHORIZED.value());
                return;
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
