package com.gora.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gora.backend.common.ResponseCode;
import com.gora.backend.model.request.LoginRequest;
import com.gora.backend.model.request.SignupRequest;
import com.gora.backend.model.response.CommonResponse;
import com.gora.backend.service.user.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public CommonResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        String accessToken = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return CommonResponse.builder().data(accessToken).build();
    }

    @PostMapping("/signup")
    public CommonResponse signup(@Valid @RequestBody SignupRequest signupRequest) {
        userService.signup(signupRequest.getEmail(), signupRequest.getPassword());
        return CommonResponse.builder().code(ResponseCode.SUCCESS.getCode()).build();
    }

    @PostMapping("/email/verify")
    public CommonResponse emailVerify(@Valid @NotBlank @RequestParam String accessToken) {
        userService.verifyToken(accessToken);
        return CommonResponse.builder().code(ResponseCode.SUCCESS.getCode()).build();
    }
}
