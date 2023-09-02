package com.gora.backend.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gora.backend.common.ResponseCode;
import com.gora.backend.exception.BadRequestException;
import com.gora.backend.model.request.LoginRequest;
import com.gora.backend.model.response.CommonResponse;
import com.gora.backend.service.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder encoder;
    @PostMapping("/login")
    public CommonResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        String accessToken = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        if(accessToken == null){
            throw new BadRequestException(ResponseCode.BAD_REQUEST);
        }

        return CommonResponse.builder().data(accessToken).build();
    }
}
