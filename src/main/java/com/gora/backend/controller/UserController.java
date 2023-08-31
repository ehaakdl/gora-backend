package com.gora.backend.controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gora.backend.model.request.LoginRequest;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    
    @PostMapping("/login")
    public LoginRequest login(LoginRequest loginRequest){
        return loginRequest;
    }
    
}
