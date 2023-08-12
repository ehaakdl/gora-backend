package com.gora.backend.common.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum eToken {
    ACCESS(
            1000L * 60 * 60
            , "access"
    )
    , REFRESH(1000L * 60 * 60
            , "refresh"
    );

    private final long expirePeriod;
    private final String subject;
}
