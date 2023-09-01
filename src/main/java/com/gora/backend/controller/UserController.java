package com.gora.backend.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gora.backend.common.ResponseCode;
import com.gora.backend.common.TokenClaimsName;
import com.gora.backend.common.token.TokenUtils;
import com.gora.backend.common.token.eTokenType;
import com.gora.backend.exception.BadRequestException;
import com.gora.backend.model.TokenInfo;
import com.gora.backend.model.entity.TokenEntity;
import com.gora.backend.model.entity.UserEntity;
import com.gora.backend.model.request.LoginRequest;
import com.gora.backend.model.response.CommonResponse;
import com.gora.backend.repository.TokenRepository;
import com.gora.backend.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {
    private final TokenUtils tokenUtils;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    // todo service 객체로 분리
    @PostMapping("/login")
    @Transactional
    public CommonResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        // todo 계정 가져오는거 함수분리
        // 이메일 존재 여부
        UserEntity user = userRepository.findByEmailAndDisable(loginRequest.getEmail(), false).orElseThrow(() -> {
            return new BadRequestException(ResponseCode.BAD_REQUEST, null);
        });
        
        // 토큰 저장
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(TokenClaimsName.EMAIL, loginRequest.getEmail());
        TokenInfo accessTokenInfo = tokenUtils.createToken(claimsMap, eTokenType.ACCESS);
        TokenInfo refreshTokenInfo = tokenUtils.createToken(claimsMap, eTokenType.REFRESH);
        tokenRepository.save(
                TokenEntity.createAccessToken(
                        user, accessTokenInfo.getToken(), refreshTokenInfo.getToken(), accessTokenInfo.getExpiredAt()));

        return CommonResponse.builder().data(accessTokenInfo.getToken()).build();
    }

}
