package com.gora.backend.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class CommonResponse {
    private Object data;
    private int code;
    private String message;

    public static CommonResponse createEmpty(){
        return CommonResponse.builder().build();
    }
}
