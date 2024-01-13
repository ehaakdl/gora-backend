package com.gora.backend.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
// Todo RestAPI 요청 함수 리팩토링하기
// 응답 클래스, 요청 header에 따른 입력값 처리
// 재사용 가능하게 설계하기

@Service
@RequiredArgsConstructor
public class WebClientService {

    private final WebClient webClient;

    public <T> Mono<T> sendGetRequest(String url, Class<T> responseType) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(responseType);
    }

    public <T> Mono<T> sendPostRequest(String url, MultiValueMap<String, String> formMap,
            Class<T> responseType) {

        return webClient.post()
                .uri(url)
                .bodyValue(formMap)
                .retrieve()
                .bodyToMono(responseType);
    }

    public <T> Mono<T> sendPostRequest(String url, Object request, Class<T> responseType) {
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(responseType);

    }

}