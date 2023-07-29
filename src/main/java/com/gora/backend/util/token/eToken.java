package com.gora.backend.util.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.gora.backend.constant.ClaimsName.EMAIL;

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
