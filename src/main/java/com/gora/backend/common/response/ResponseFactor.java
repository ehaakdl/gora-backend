package com.gora.backend.common.response;

import org.springframework.stereotype.Component;

import com.gora.backend.common.ResponseCode;
import com.gora.backend.model.response.ErrorResponse;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

@Component
@RequiredArgsConstructor
public class ResponseFactor {
    private final MessageSource messageSource;

    public ErrorResponse createEmptyErrorResponse(){
        return ErrorResponse.builder().errorCode(ResponseCode.I_DONT_KWON.getCode()).message("").build();
    }

    // todo @notnull체크
    public ErrorResponse createErrorResponse(@NotNull ResponseCode responseCode, @NotNull String messageCode){
        String message = messageSource.getMessage(messageCode, null, null, null);
        if(message == null){
            return null;
        }

        return ErrorResponse.builder().errorCode(responseCode.getCode()).message(message).build();
    }
}
