package com.gora.backend.service.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gora.backend.common.TokenClaimsName;
import com.gora.backend.common.token.TokenUtils;
import com.gora.backend.common.token.eTokenType;
import com.gora.backend.model.TokenInfo;
import com.gora.backend.model.entity.TokenEntity;
import com.gora.backend.model.entity.UserEntity;
import com.gora.backend.model.entity.eUserType;
import com.gora.backend.repository.TokenRepository;
import com.gora.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final TokenUtils tokenUtils;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String login(String email, String password){
        UserEntity user = userRepository.findByEmailAndType(email,eUserType.basic).orElse(null);
        if(user == null){
            return null;
        }
        
        if(!passwordEncoder.matches(password, user.getPassword())){
            return null;
        }

        // 토큰 저장
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(TokenClaimsName.EMAIL, email);
        TokenInfo accessTokenInfo = tokenUtils.createToken(claimsMap, eTokenType.ACCESS);
        TokenInfo refreshTokenInfo = tokenUtils.createToken(claimsMap, eTokenType.REFRESH);
        tokenRepository.save(
                TokenEntity.createAccessToken(
                        user, accessTokenInfo.getToken(), refreshTokenInfo.getToken(), accessTokenInfo.getExpiredAt()));

        return accessTokenInfo.getToken();
    }
    
}
