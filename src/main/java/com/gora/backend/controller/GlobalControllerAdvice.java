package com.gora.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.gora.backend.common.response.ResponseFactor;
import com.gora.backend.model.response.ErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    private final ResponseFactor responseFactor;
    @ExceptionHandler(Exception.class)
    private ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("500 error:", ex);
        
        ErrorResponse responseModel = responseFactor.createErrorResponse(null, "error.server");
        if(responseModel == null){
            log.error("에러 응답 모델 생성 실패했습니다.");
            return ResponseEntity.internalServerError().body(responseFactor.createEmptyErrorResponse());
        }

        return ResponseEntity.internalServerError().body(responseModel);
    } 
    
}
