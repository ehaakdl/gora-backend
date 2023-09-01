package com.gora.backend.filter;

import java.io.IOException;

import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gora.backend.common.ResponseCode;
import com.gora.backend.exception.BadRequestException;
import com.gora.backend.model.response.ErrorResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch(BadRequestException e){
            log.error("잘못된 요청 발생: ", e);
            setErrorResponse(response, e.getCode(), e.getMessage());
        } catch(Exception e){
            log.error("알 수 없는 에러 발생: ", e);
            setErrorResponse(response, ResponseCode.I_DONT_KWON, "error.serverInternal");
        }
    }

    private void setErrorResponse(
            HttpServletResponse response,
            ResponseCode errorCode,
            String messageCode) {
        response.setStatus(errorCode.getHttpStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String message = messageSource.getMessage(messageCode, null, null);
        ErrorResponse errorResponse = ErrorResponse.create(errorCode.getCode(), message);

        try {
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            log.error("알 수 없는 에러 발생: ", e);
        }
    }
}
