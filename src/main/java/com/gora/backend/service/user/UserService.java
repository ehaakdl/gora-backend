package com.gora.backend.service.user;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gora.backend.common.EnvironmentKey;
import com.gora.backend.common.FrontUrl;
import com.gora.backend.common.ResponseCode;
import com.gora.backend.common.RoleCode;
import com.gora.backend.common.token.TokenCreator;
import com.gora.backend.common.token.TokenUtils;
import com.gora.backend.common.token.eTokenType;
import com.gora.backend.exception.BadRequestException;
import com.gora.backend.model.EmailMessage;
import com.gora.backend.model.LoginTokenPair;
import com.gora.backend.model.TokenInfoDto;
import com.gora.backend.model.response.oauth2.GoogleUserProfile;
import com.gora.backend.repository.EmailVerifyCustomRepository;
import com.gora.backend.service.EmailService;
import com.gora.common.model.entity.EmailVerifyEntity;
import com.gora.common.model.entity.RoleEntity;
import com.gora.common.model.entity.SocialUserEntity;
import com.gora.common.model.entity.TokenEntity;
import com.gora.common.model.entity.UserEntity;
import com.gora.common.model.entity.UserRoleEntity;
import com.gora.common.model.entity.eSocialType;
import com.gora.common.model.entity.eTokenUseDBType;
import com.gora.common.model.entity.eUserType;
import com.gora.common.repository.EmailVerifyRepository;
import com.gora.common.repository.RoleRepository;
import com.gora.common.repository.SocialUserRepository;
import com.gora.common.repository.TokenRepository;
import com.gora.common.repository.UserRepository;
import com.gora.common.repository.UserRoleRepository;

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
    private final SocialUserRepository socialUserRepository;
    private final EmailService emailService;
    private final TokenCreator tokenCreator;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailVerifyCustomRepository emailVerifyCustomRepository;

    @Transactional
    public UserEntity upsertSocialUser(String email) {
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        return Objects.requireNonNullElseGet(user, () -> {
            UserEntity _user = userRepository.save(
                    UserEntity.createSocialUser(email));

            eSocialType socialType = eSocialType.convert(email);
            if (socialType == null) {
                throw new RuntimeException();
            }

            SocialUserEntity socialUserEntity = SocialUserEntity.builder().updatedBy(-1L).updatedAt(new Date())
                    .createdBy(-1L)
                    .createdAt(new Date()).socialType(socialType).build();
            socialUserRepository
                    .save(socialUserEntity);

            RoleEntity roleEntity = roleRepository.findByCode(RoleCode.ROLE_PUBLIC)
                    .orElseThrow(() -> new RuntimeException());

            userRoleRepository.save(UserRoleEntity.create(_user, roleEntity));
            return _user;
        });
    }

    @Transactional
    public void upsertOauth2Token(UserEntity userEntity, String socialAccessToken, Date socialAccessTokenExpiredAt) {
        List<TokenEntity> tokenResults = tokenRepository.findByUserAndType(userEntity, eTokenUseDBType.oauth_token);
        if (tokenResults.size() > 0) {
            tokenRepository.deleteByUserAndType(userEntity, eTokenUseDBType.oauth_token);
        }
        TokenEntity tokenEntity = TokenEntity.createOauth2Token(
                socialAccessToken, socialAccessTokenExpiredAt, userEntity);
        tokenRepository.save(tokenEntity);
    }

    @Transactional
    public String getSocialLoginToken(
            String socialAccessToken, Date socialAccessTokenExpiredAt, GoogleUserProfile profile) {
        upsertSocialUser(profile.getEmail());

        LoginTokenPair loginTokenPair = tokenCreator.createLoginToken(profile.getEmail(), eUserType.social);
        if (loginTokenPair == null) {
            throw new BadRequestException(ResponseCode.BAD_REQUEST);
        }

        UserEntity userEntity = userRepository.findByEmail(profile.getEmail())
                .orElseThrow(() -> new RuntimeException());
        // oauth2 토큰 저장 나중에 개인정보 가져올때 사용할 예정
        upsertOauth2Token(userEntity, socialAccessToken, socialAccessTokenExpiredAt);

        return loginTokenPair.getAccess();
    }

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
}
