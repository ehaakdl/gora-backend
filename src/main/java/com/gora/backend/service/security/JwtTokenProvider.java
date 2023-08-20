package com.gora.backend.service.security;

import static com.gora.backend.common.ClaimsName.*;

import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.gora.backend.common.token.TokenUtils;
import com.gora.backend.exception.BadRequestException;
import com.gora.backend.model.entity.TokenEntity;
import com.gora.backend.repository.TokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenRepository tokenRepository;
    private final TokenUtils tokenUtils;

    public Authentication getAuthentication(String token) {
        TokenEntity tokenEntity = tokenRepository.findByAccess(token).orElse(null);
        if(tokenEntity == null){
            return null;
        }

        String email = Optional.ofNullable(tokenUtils.getValue(token.replace("Bearer ", ""), EMAIL))
                .orElseThrow(BadRequestException::new);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }
}