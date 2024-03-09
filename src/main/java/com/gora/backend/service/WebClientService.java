package com.gora.backend.service;

// Todo RestAPI 요청 함수 리팩토링하기
// 응답 클래스, 요청 header에 따른 입력값 처리
// 재사용 가능하게 설계하기
import java.util.function.Consumer;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WebClientService {

    private final WebClient webClient;

    public <T> Mono<T> sendGetRequest(@NonNull Consumer<HttpHeaders> headers, UriComponents uriComponents,
            @NonNull Class<T> responseType) {
        String url = uriComponents.getScheme() + "://" + uriComponents.getHost() + uriComponents.getPath();
        return webClient.get()
                .uri(url)
                .headers(headers)
                .retrieve()
                .bodyToMono(responseType);
    }

    public <T> Mono<T> sendPostRequest(@NonNull String url, MultiValueMap<String, String> formMap,
            Class<T> responseType) {

        return webClient.post()
                .uri(url)
                .bodyValue(formMap)
                .retrieve()
                .bodyToMono(responseType);
    }

    public <T> Mono<T> sendPostRequest(@NonNull String url, @NonNull Object request, @NonNull Class<T> responseType) {
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(responseType);

    }

}