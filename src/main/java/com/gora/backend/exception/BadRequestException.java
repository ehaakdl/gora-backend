package com.gora.backend.exception;

import com.gora.backend.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BadRequestException extends RuntimeException{
    private final ResponseCode code;
    private final String message;
}
