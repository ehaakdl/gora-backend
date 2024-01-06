package com.gora.backend.service;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class WebClientExample {

    private final WebClientService webClientService;

    public void makeRequest() {
        String url = "https://www.googleapis.com/oauth2/v4/token";
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("scope", "");
        body.add("client_secret", "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");
        body.add("code_verifier", "RxrwPdz5gcT2eOgcMuSCUIdPSrTH8GDZ1qUOOdTocUY");
        body.add("client_id", "843307007567-4l6dtj4a0kl5fdi3meh51u1tko7fu7l7.apps.googleusercontent.com");
        body.add("redirect_uri", "http://localhost:8080/login/oauth2/code/google");
        body.add("code", "4/0AfJohXl2smFtdCpz-Rr82TO6MMnv6gjsD6_gbIxLrWY58nZbc1S5ZfbH60N3RNSMlszRfA");
        // code=4/0AfJohXmaMv7Qwdp8Zc0G1mK2STiuGe8d98GKVcKv0LBSvuIrj9dZKP4sMoYT2os7RWNmug&redirect_uri=http%3A%2F%2F127.0.0.1%3A51767%2F&&code_verifier=&scope=&grant_type=authorization_code
        // ={0}&redirect_uri={1}&client_id={2}&code_verifier={3}&client_secret={4}
        Mono<String> response = webClientService.sendPostRequest(url, body, String.class);

        // 비동기적 처리
        response.subscribe(System.out::println);

        // 필요한 경우 .block()을 사용하여 동기적으로 결과를 받을 수 있지만, 이는 일반적으로 권장되지 않음
        // String result = response.block();
        // System.out.println(result);
    }
}
