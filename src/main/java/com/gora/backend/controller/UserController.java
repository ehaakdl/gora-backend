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
import com.gora.backend.model.request.LoginRequest;
import com.gora.backend.model.request.SignupRequest;
import com.gora.backend.model.response.CommonResponse;
import com.gora.backend.service.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    // todo 클라이언트에서 주기적으로 토큰 체크 담당한다. 이 부분은 웹소켓으로 처리하는게 옳다. 시간없으니 임시로 웹 요청으로 처리 
    // 클라이언트에 토큰을 체크한다.
    @GetMapping("/user/auth/token-status")
    // @ResponseStatus(code = HttpStatus.OK)
    public void checkUserToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("inqewwq");  
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return;
        // if(userService.checkLoginUserToken(request.getHeader(HttpHeaders.AUTHORIZATION))){
        //     response.setStatus(HttpStatus.OK.value());
        // }else{
        //     response.setStatus(HttpStatus.UNAUTHORIZED.value());
        // }
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
