package com.gora.backend.controller;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
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
import com.gora.backend.model.response.oauth2.GoogleUserProfile;
import com.gora.backend.service.security.Oauth2UserService;
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
    private final Oauth2UserService oauth2UserService;

    // 소셜 로그인 유저 정보 저장및 토큰 발급
    /*
     * {
     * "access_token":
     * "ya29.a0AfB_byBY2HujDXTHCH0sAn1UOehfciY1IVrppQzYSIF5a6g45tHVGUQT7MtmrKg7fZXHheRP8nhJSrrmkGdNeTz9mu-ECDNOfpm6eq_u2wHydKcQkm9oe5U94KpmRiPdzmRfkDth85zidZ9RqxAYbjF739wv-8jGQN7paCgYKAdgSARESFQHGX2MidkkNrcA8NeFW0FOjbJVWBw0171",
     * "expires_in": 3599,
     * "refresh_token":
     * "1//0ezJ57ACltDmICgYIARAAGA4SNwF-L9Ir1ZfU5Cds-DjSzYuYvXjXvmp-4XTXEAU8OnpBPTuzF9g42TgMCU3434h2ONRldSFdQAM",
     * "scope": "openid https://www.googleapis.com/auth/userinfo.profile",
     * "token_type": "Bearer",
     * "id_token":
     * "eyJhbGciOiJSUzI1NiIsImtpZCI6Ijg1ZTU1MTA3NDY2YjdlMjk4MzYxOTljNThjNzU4MWY1YjkyM2JlNDQiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI4NDMzMDcwMDc1NjctNGw2ZHRqNGEwa2w1ZmRpM21laDUxdTF0a283ZnU3bDcuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI4NDMzMDcwMDc1NjctNGw2ZHRqNGEwa2w1ZmRpM21laDUxdTF0a283ZnU3bDcuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDI3OTUwMTUwMDIwNzczNjkzNDYiLCJhdF9oYXNoIjoiVUJySDFSZ1VwdEtXaVZXV1dVaUpIUSIsIm5hbWUiOiJlaGFha2RsIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL0FDZzhvY0xlYTVBWnFjLUREWGhyNFVtSFAtX21nWTBrVmNXQ2ltaDhzSDhkZnlXek1XOD1zOTYtYyIsImdpdmVuX25hbWUiOiJlaGFha2RsIiwibG9jYWxlIjoia28iLCJpYXQiOjE3MDYyNjI0NTMsImV4cCI6MTcwNjI2NjA1M30.QZu0qJiERTbrEWenG9xSTbqcOJOP-ENppWqYuri4qondjOGk8AQpwyYvpfSAoozyShM4IwhGYLuSXgjw6UP0ZO8B-LkNZcIfD8DSKGPkqSV37fXETjQP9xAY0V0ZpulubdJTPaT1l0aIQsENjwl1lqhmnLnZashG8EGpD1phjPInZvUTazZkUTPmR0JvgrtAK0AxAvG1nXxEJgtFfMC8FQJXnUhxmXu3RtvTGwWGDxwTKj9d1PzIE6zttSoWQPGx2oACSNYtnHknyHbUSBoahsRxv8zxtOJ19t6nkFi2isLXrHyptBs2HYR7N6XuybFU6aajYXgr1_OJrGpTO0gylA"
     * }
     */

    @GetMapping("/oauth2/authorize")
    public CommonResponse getLoginTokenBySocialUser(
            @RequestParam String registrationId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date expiredAt,
            @RequestParam String socialAccessToken) {

        GoogleUserProfile profile = oauth2UserService.loadUser(registrationId, socialAccessToken);

        String token = userService.getSocialLoginToken(socialAccessToken, expiredAt, profile);
        if (token == null) {
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
