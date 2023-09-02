package com.gora.backend.exception;

import com.gora.backend.common.ResponseCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BadRequestException extends RuntimeException{
    private final ResponseCode errorCode;
    private String messageCode;

    public BadRequestException(ResponseCode errorCode){
        this.errorCode = errorCode;
    }
}
