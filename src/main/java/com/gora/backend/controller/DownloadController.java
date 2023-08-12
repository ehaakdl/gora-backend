package com.gora.backend.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/download")
public class DownloadController {
    @GetMapping("/client")
    public ResponseEntity<InputStreamResource> downloadClient() throws IOException {
        Resource resource = new ClassPathResource("game-client.zip");
        File file = resource.getFile();

        // Set the appropriate headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getName());

        // Create the InputStreamResource from the file
        InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(file));

        // Return the ResponseEntity
        return ResponseEntity.ok()
                .headers(headers)
                .body(inputStreamResource);
    }
}
