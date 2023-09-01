package com.gora.backend.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResponseCode {
    I_DONT_KWON(1, HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST(2, HttpStatus.BAD_REQUEST);
    private final int code;
    private final HttpStatusCode httpStatusCode;
}
