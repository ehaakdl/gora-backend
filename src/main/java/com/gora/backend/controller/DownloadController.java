package com.gora.backend.controller;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gora.backend.common.CommonUtils;
import com.gora.backend.common.ResponseCode;
import com.gora.backend.exception.BadRequestException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/download")
@RequiredArgsConstructor
@Slf4j
public class DownloadController {
    private final CommonUtils commonUtils;

    // 파일형식이 다르다 런타임과 jar 실행은 그래서 loadResourceAsStream 함수 호출 필요함
    @GetMapping("/client")
    public ResponseEntity<Resource> downloadClient() throws IOException {
        String tempFilePath = "./game-client.zip";
        String resourceFilePath =  "/static/game-client.zip";
        Resource resource = commonUtils.convertFileToResource(resourceFilePath, tempFilePath);
        if (resource == null) {
            throw new BadRequestException(ResponseCode.I_DONT_KWON);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "client.zip" + "\"")
                .body(resource);
    }

}