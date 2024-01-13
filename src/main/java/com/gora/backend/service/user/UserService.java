package com.gora.backend.service.user;

import java.util.Date;
import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gora.backend.common.EnvironmentKey;
import com.gora.backend.common.FrontUrl;
import com.gora.backend.common.ResponseCode;
import com.gora.backend.common.token.TokenCreator;
import com.gora.backend.common.token.TokenUtils;
import com.gora.backend.common.token.eTokenType;
import com.gora.backend.exception.BadRequestException;
import com.gora.backend.model.EmailMessage;
import com.gora.backend.model.LoginTokenPair;
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
// Note 승인코드 받기, 코드와 교환하여 구글 액세스 토큰 받기, 구글 계정 정보 가져오기, 
/*
 * private final SocialUserRepository socialUserRepository;

    @RequiredArgsConstructor
    @Getter
    class SocialUserDTO {
        private final String email;
    }

    public SocialUserDTO getSocialUserInfoByOauthAccessToken(String oauthAccessToken) {
        return new SocialUserDTO("test@email.com");
    }

    @Transactional
    public UserEntity saveSocialUser(SocialUserDTO socialUser) {

        UserEntity user = userRepository.findByEmailAndType(socialUser.getEmail(), eUserType.social).orElse(null);
        return Objects.requireNonNullElseGet(user, () -> {
            UserEntity _user = userRepository.save(
                    UserEntity.createSocialUser(socialUser.getEmail()));

            eSocialType socialType = eSocialType.convert(socialUser.getEmail());
            if (socialType == null) {
                throw new RuntimeException();
            }

            socialUserRepository
                    .save(SocialUserEntity.builder().updatedBy(-1L).updatedAt(new Date()).createdBy(-1L)
                            .createdAt(new Date()).socialType(socialType).build());

            // Date expiredAt = Date.from(oauth2Token.getExpiresAt());
            // tokenRepository.save(TokenEntity.createOauthToken(token, expiredAt, user));

            return _user;
        });
    }
 */
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
    private final TokenCreator tokenCreator;

    @Transactional
    public String login(String email, String password) {
        UserEntity user = userRepository.findByEmailAndType(email, eUserType.basic).orElse(null);
        if (user == null) {
            throw new BadRequestException(ResponseCode.BAD_REQUEST);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException(ResponseCode.BAD_REQUEST);
        }

        // 토큰 저장
        LoginTokenPair loginTokenPair = tokenCreator.createLoginToken(email, eUserType.basic);
        if (loginTokenPair == null) {
            throw new BadRequestException(ResponseCode.BAD_REQUEST);
        }

        return loginTokenPair.getAccess();
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
    public void verifyEmailToken(String accessToken) {
        Date nowAt = new Date();

        TokenEntity tokenEntity = tokenRepository
                .findByAccessAndTypeAndAccessExpireAtAfter(accessToken, eTokenUseDBType.email_verify, nowAt)
                .orElse(null);
        if (tokenEntity == null) {
            throw new BadRequestException(ResponseCode.EXPIRED);
        }

        EmailVerifyEntity emailVerifyEntity = tokenEntity.getEmailVerify();
        if (emailVerifyEntity == null) {
            throw new BadRequestException(ResponseCode.BAD_REQUEST);
        }

        Date verifiedExpiredAt = new Date(nowAt.getTime() + eTokenType.EMAIL_VERIFY.getExpirePeriod());
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

    @Transactional
    public String getSocialUserProfile(@Valid @NotBlank String socialToken) {
        // 소셜 토큰 받기
        // 디비에 넣기
        // 이메일 기반 유저 생성
        // 토큰 발급
        Date nowAt = new Date();
        TokenEntity tokenEntity = tokenRepository
                .findByAccessAndTypeAndAccessExpireAtAfter(token, eTokenUseDBType.oauth_token, nowAt)
                .orElse(null);

        if (tokenEntity == null) {
            return null;
        }
        
        tokenEntity = tokenRepository.findByUserAndTypeAndAccessExpireAtAfter(tokenEntity.getUser(),
                eTokenUseDBType.login, nowAt).orElse(null);
        if(tokenEntity == null){
            return null;
        }

        return tokenEntity.getAccess();
    }
}
