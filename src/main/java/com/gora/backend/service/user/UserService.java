package com.gora.backend.service.user;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gora.backend.common.ResponseCode;
import com.gora.backend.common.TokenClaimsName;
import com.gora.backend.common.token.TokenUtils;
import com.gora.backend.common.token.eTokenType;
import com.gora.backend.exception.BadRequestException;
import com.gora.backend.model.TokenInfo;
import com.gora.backend.model.entity.EmailVerifyEntity;
import com.gora.backend.model.entity.TokenEntity;
import com.gora.backend.model.entity.UserEntity;
import com.gora.backend.model.entity.eTokenUseDBType;
import com.gora.backend.model.entity.eUserType;
import com.gora.backend.repository.EmailVerifyRepository;
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
    private final EmailVerifyRepository emailVerifyRepository;

    @Transactional
    public String login(String email, String password){
        UserEntity user = userRepository.findByEmailAndType(email,eUserType.basic).orElse(null);
        if(user == null){
            throw new BadRequestException(ResponseCode.BAD_REQUEST);
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
                TokenEntity.createLoginToken(
                        user, accessTokenInfo.getToken(), refreshTokenInfo.getToken(), accessTokenInfo.getExpiredAt()));

        return accessTokenInfo.getToken();
    }

    @Transactional
    public void signup(String email, String password){
        if(userRepository.existsByEmail(email)){
            throw new BadRequestException(ResponseCode.EXISTS_EMAIL);
        }
        EmailVerifyEntity emailVerifyEntityLastest = emailVerifyRepository.findTopByEmailOrderByVerifiedExpireAt(email).orElse(null);
        if(emailVerifyEntityLastest == null){
            throw new BadRequestException(ResponseCode.EXPIRED);
        }

        Date nowAt = new Date();
        if(emailVerifyEntityLastest.getVerifiedExpireAt().getTime() < nowAt.getTime()){
            throw new BadRequestException(ResponseCode.EXPIRED);
        }

        List<EmailVerifyEntity> emailVerifyEntities = emailVerifyRepository.findAllByEmail(email);
        for (EmailVerifyEntity emailVerifyEntity : emailVerifyEntities) {
            emailVerifyRepository.delete(emailVerifyEntity);
        }

        userRepository.save(UserEntity.createBasicUser(passwordEncoder.encode(password), email));
    }

    @Transactional
    public void verifyToken(String accessToken) {
        TokenEntity tokenEntity = tokenRepository.findByAccessAndTypeAndAccessExpireAtAfter(accessToken, eTokenUseDBType.email_verify, new Date()).orElse(null);
        if(tokenEntity == null){
            throw new BadRequestException(ResponseCode.EXPIRED);
        }

        EmailVerifyEntity emailVerifyEntity = tokenEntity.getEmailVerify();
        if(emailVerifyEntity == null){
            throw new BadRequestException(ResponseCode.BAD_REQUEST);
        }

        final long VALID_TIME = 1000 * 60 * 3;
        Date verifiedExpiredAt = new Date(System.currentTimeMillis() + VALID_TIME);
        emailVerifyEntity.setVerifiedExpireAt(verifiedExpiredAt);
    }
    
    @Transactional
    public void createVerifyToken(String email) {
        TokenInfo tokenInfo = tokenUtils.createToken(null, eTokenType.EMAIL_VERIFY);

        TokenEntity tokenEntity = TokenEntity.createEmailVerifyToken(null, tokenInfo.getToken(), tokenInfo.getExpiredAt());
        EmailVerifyEntity emailVerifyEntity = EmailVerifyEntity.create(tokenEntity, email, null);
        emailVerifyRepository.save(emailVerifyEntity);
    }
}
