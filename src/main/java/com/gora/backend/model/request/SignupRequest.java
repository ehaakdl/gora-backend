package com.gora.backend.model.request;

import com.gora.backend.validation.PasswordCheck;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupRequest {
    @Email
    @NotBlank
    private String email;
    
    @PasswordCheck
    @NotBlank
    private String password;
}
