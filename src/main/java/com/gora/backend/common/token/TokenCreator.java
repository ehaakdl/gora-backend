package com.gora.backend.common.token;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gora.backend.common.TokenClaimsName;
import com.gora.backend.model.LoginTokenPair;
import com.gora.backend.model.TokenInfoDto;
import com.gora.backend.model.entity.TokenEntity;
import com.gora.backend.model.entity.UserEntity;
import com.gora.backend.model.entity.eTokenUseDBType;
import com.gora.backend.model.entity.eUserType;
import com.gora.backend.repository.TokenRepository;
import com.gora.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenCreator {
    private final TokenUtils tokenUtils;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public LoginTokenPair createLoginToken(String email, eUserType userType) {
        UserEntity user = userRepository.findByEmailAndType(email, userType).orElse(null);
        if (user == null) {
            return null;
        }

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(TokenClaimsName.EMAIL, email);
        claimsMap.put(TokenClaimsName.RANDOM_NUMBER, UUID.randomUUID().toString().replace("-", ""));
        TokenInfoDto accessTokenInfo = tokenUtils.createToken(claimsMap, eTokenType.ACCESS);
        TokenInfoDto refreshTokenInfo = tokenUtils.createToken(claimsMap, eTokenType.REFRESH);
        tokenRepository.save(
                TokenEntity.createLoginToken(
                        user, accessTokenInfo.getToken(), refreshTokenInfo.getToken(), accessTokenInfo.getExpiredAt()));

        return new LoginTokenPair(accessTokenInfo.getToken(), refreshTokenInfo.getToken());
    }

    @Transactional
    public LoginTokenPair refreshLoginToken(String token) {
        TokenEntity tokenEntity = tokenRepository.findByAccessAndType(token, eTokenUseDBType.login).orElse(null);
        if (tokenEntity == null) {
            return null;
        }
        UserEntity userEntity = tokenEntity.getUser();

        if (tokenUtils.isExpired(tokenEntity.getRefresh())) {
            return null;
        }

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(TokenClaimsName.EMAIL, userEntity.getEmail());
        claimsMap.put(TokenClaimsName.RANDOM_NUMBER, UUID.randomUUID().toString().replace("-", ""));
        TokenInfoDto accessTokenInfo = tokenUtils.createToken(claimsMap, eTokenType.ACCESS);
        TokenInfoDto refreshTokenInfo = tokenUtils.createToken(claimsMap, eTokenType.REFRESH);
        tokenRepository.save(
                TokenEntity.createLoginToken(
                        userEntity, accessTokenInfo.getToken(), refreshTokenInfo.getToken(),
                        accessTokenInfo.getExpiredAt()));

        tokenRepository.delete(tokenEntity);
        return new LoginTokenPair(accessTokenInfo.getToken(), refreshTokenInfo.getToken());
    }

}
