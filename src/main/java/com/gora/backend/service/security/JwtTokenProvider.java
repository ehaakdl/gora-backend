package com.gora.backend.service.security;

import com.gora.backend.exception.BadRequestException;
import com.gora.backend.model.entity.TokenEntity;
import com.gora.backend.repository.TokenRepository;
import com.gora.backend.util.token.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static com.gora.backend.constant.ClaimsName.EMAIL;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenRepository tokenRepository;
    private final TokenUtils tokenUtils;

    public Authentication getAuthentication(String token) {
        TokenEntity tokenEntity = tokenRepository.findById(token).orElse(null);
        if(tokenEntity == null){
            return null;
        }

        String email = Optional.ofNullable(tokenUtils.getValue(token, EMAIL))
                .orElseThrow(BadRequestException::new);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }
}