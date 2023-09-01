package com.gora.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gora.backend.model.request.LoginRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @PostMapping("/login")
    public LoginRequest login(@Valid @RequestBody LoginRequest loginRequest) {
        return loginRequest;
    }

}
