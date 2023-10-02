package com.gora.backend.common.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum eTokenType {
    ACCESS(
            1000L * 60 * 60
            , "access"
    )
    , REFRESH(1000L * 60 * 60
            , "refresh"
    )
    , EMAIL_VERIFY(
        1000L * 60 * 3
        , "email_verify"
    ),
    CLIENT_IDENTIFIER(
            1000L * 60 * 60 * 24 * 365
            , "access"
    );

    private final long expirePeriod;
    private final String subject;
}
