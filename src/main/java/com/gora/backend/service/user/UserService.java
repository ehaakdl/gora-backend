package com.gora.backend.service.user;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gora.backend.common.EnvironmentKey;
import com.gora.backend.common.FrontUrl;
import com.gora.backend.common.ResponseCode;
import com.gora.backend.common.TokenClaimsName;
import com.gora.backend.common.token.TokenUtils;
import com.gora.backend.common.token.eTokenType;
import com.gora.backend.exception.BadRequestException;
import com.gora.backend.model.EmailMessage;
import com.gora.backend.model.TokenInfoDto;
import com.gora.backend.model.entity.EmailVerifyEntity;
import com.gora.backend.model.entity.TokenEntity;
import com.gora.backend.model.entity.UserEntity;
import com.gora.backend.model.entity.eTokenUseDBType;
import com.gora.backend.model.entity.eUserType;
import com.gora.backend.repository.EmailVerifyCustomRepository;
import com.gora.backend.repository.EmailVerifyRepository;
import com.gora.backend.repository.TokenRepository;
import com.gora.backend.repository.UserRepository;
import com.gora.backend.service.EmailService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final TokenUtils tokenUtils;
    private final Environment environment;
    private final TokenRepository tokenRepository;
    private final MessageSource messageSource;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerifyRepository emailVerifyRepository;
    private final EmailVerifyCustomRepository emailVerifyCustomRepository;
    private final EmailService emailService;

    @Transactional
    public String login(String email, String password){
        UserEntity user = userRepository.findByEmailAndType(email,eUserType.basic).orElse(null);
        if(user == null){
            throw new BadRequestException(ResponseCode.BAD_REQUEST);
        }
        
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new BadRequestException(ResponseCode.BAD_REQUEST);
        }

        // 토큰 저장
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(TokenClaimsName.EMAIL, email);
        TokenInfoDto accessTokenInfo = tokenUtils.createToken(claimsMap, eTokenType.ACCESS);
        TokenInfoDto refreshTokenInfo = tokenUtils.createToken(claimsMap, eTokenType.REFRESH);
        tokenRepository.save(
                TokenEntity.createLoginToken(
                        user, accessTokenInfo.getToken(), refreshTokenInfo.getToken(), accessTokenInfo.getExpiredAt()));

        return accessTokenInfo.getToken();
    }

    @Transactional
    public void signup(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException(ResponseCode.EXISTS_EMAIL);
        }

        if (!emailVerifyCustomRepository.existsEmailVerified(email)) {
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
        TokenEntity tokenEntity = tokenRepository
                .findByAccessAndTypeAndAccessExpireAtAfter(accessToken, eTokenUseDBType.email_verify, new Date())
                .orElse(null);
        if (tokenEntity == null) {
            throw new BadRequestException(ResponseCode.EXPIRED);
        }

        EmailVerifyEntity emailVerifyEntity = tokenEntity.getEmailVerify();
        if (emailVerifyEntity == null) {
            throw new BadRequestException(ResponseCode.BAD_REQUEST);
        }

        final long VALID_TIME = 1000 * 60 * 3;
        Date verifiedExpiredAt = new Date(System.currentTimeMillis() + VALID_TIME);
        emailVerifyEntity.setVerifiedExpireAt(verifiedExpiredAt);
    }

    @Transactional
    public void sendVerifyEmail(@Valid @NotBlank @Email String email) {
        TokenInfoDto tokenInfo = tokenUtils.createToken(null, eTokenType.EMAIL_VERIFY);

        EmailVerifyEntity emailVerifyEntity = EmailVerifyEntity.builder().email(email).build();
        TokenEntity tokenEntity = TokenEntity.createEmailVerifyToken(emailVerifyEntity, tokenInfo.getToken(),
                tokenInfo.getExpiredAt());
        tokenRepository.save(tokenEntity);

        String emailVerifyUrl = environment.getProperty(EnvironmentKey.APP_FRONT_URL) + FrontUrl.EMAIL_VERIFY
                + "?accessToken=" + tokenInfo.getToken();
        String subject = messageSource.getMessage("email.verifyMail.subject", null, null);
        emailService.send(EmailMessage.create(email, emailVerifyUrl, subject));
    }

    public boolean checkLoginUserToken(@Valid @NotBlank String token) {
        TokenEntity tokenEntity = tokenRepository.findByAccessAndTypeAndAccessExpireAtAfter(token, eTokenUseDBType.login, new Date())
        .orElse(null);

        if(tokenEntity == null){
            return false;
        }else{
            return true;
        }
    }
}
