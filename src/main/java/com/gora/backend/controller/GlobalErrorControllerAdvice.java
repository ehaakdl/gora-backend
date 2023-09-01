package com.gora.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.gora.backend.common.ResponseCode;
import com.gora.backend.common.response.ResponseFactory;
import com.gora.backend.exception.BadRequestException;
import com.gora.backend.model.response.ErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// todo 에러 발생시 email noti
@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalErrorControllerAdvice {
    private final ResponseFactory responseFactory;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    private ErrorResponse serverInternal(Exception ex) {
        log.error("서버에서 처하 못한에러 발생:", ex);

        ErrorResponse responseModel;
        try {
            responseModel = responseFactory.createErrorResponse(ResponseCode.I_DONT_KWON, "error.serverInternal");
        } catch (Exception exception) {
            log.error("에러 응답 모델 생성 실패했습니다.");
            return responseFactory.createEmptyErrorResponse();
        }

        return responseModel;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private ErrorResponse badRequest(Exception ex) {
        log.error("잘못된 요청 발생:", ex);

        ErrorResponse responseModel;
        try {
            responseModel = responseFactory.createErrorResponse(ResponseCode.BAD_REQUEST, "error.badRequest");
        } catch (Exception exception) {
            log.error("에러 응답 모델 생성 실패했습니다.");
            return responseFactory.createEmptyErrorResponse();
        }

        return responseModel;
    }
}
