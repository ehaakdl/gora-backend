package com.gora.backend.service.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import com.gora.backend.common.ResponseCode;
import com.gora.backend.exception.BadRequestException;
import com.gora.backend.model.entity.TokenEntity;
import com.gora.backend.repository.TokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final UserDetailsServiceImpl UserDetailsServiceImpl;
    private final TokenRepository tokenRepository;
    
    @Transactional
    public Authentication getAuthentication(String token) {
        TokenEntity tokenEntity = tokenRepository.findByAccess(token).orElse(null);
        if(tokenEntity == null){
            throw new BadRequestException(ResponseCode.BAD_REQUEST, "error.badRequest");
        }
        
        String email = tokenEntity.getUser().getEmail();
        UserDetails userDetails = UserDetailsServiceImpl.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), token, userDetails.getAuthorities());
    }
}