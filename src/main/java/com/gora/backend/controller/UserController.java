package com.gora.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gora.backend.common.ResponseCode;
import com.gora.backend.exception.BadRequestException;
import com.gora.backend.model.request.LoginRequest;
import com.gora.backend.model.request.SignupRequest;
import com.gora.backend.model.response.CommonResponse;
import com.gora.backend.service.user.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/user/social/accessToken")
    @ResponseStatus(code = HttpStatus.OK)
    public CommonResponse getOauthUserToken(@Valid @RequestParam String accessToken) {
        
        String token = userService.getSocialUserLoginToken(accessToken);
        if(token == null){
            throw new BadRequestException(ResponseCode.BAD_REQUEST);
        }
        
        return CommonResponse.builder().data(token).build();
    }

    @PostMapping("/login")
    public CommonResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        String accessToken = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return CommonResponse.builder().data(accessToken).build();
    }

    @PostMapping("/signup")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommonResponse signup(@Valid @RequestBody SignupRequest signupRequest) {
        userService.signup(signupRequest.getEmail(), signupRequest.getPassword());
        return CommonResponse.builder().code(ResponseCode.SUCCESS.getCode()).build();
    }

    @PostMapping("/user/email-verify")
    public CommonResponse emailVerify(@Valid @NotBlank @RequestParam String accessToken) {
        userService.verifyEmailToken(accessToken);
        return CommonResponse.builder().code(ResponseCode.SUCCESS.getCode()).build();
    }

    @PostMapping("/user/email/verify-send")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommonResponse emailVerifySend(@Valid @NotBlank @Email @RequestParam String email) {
        userService.sendVerifyEmail(email);
        return CommonResponse.builder().code(ResponseCode.SUCCESS.getCode()).build();
    }
}
