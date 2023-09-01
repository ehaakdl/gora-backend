package com.gora.backend.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse{
    private String message;
    private int errorCode;

    public static ErrorResponse create(int code, String message){
        return ErrorResponse.builder().errorCode(code).message(message).build();
    }
}