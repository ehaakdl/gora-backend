package com.gora.backend.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResponseCode {
    I_DONT_KWON(1),
    BAD_REQUEST(2);
    private final int code;
}
