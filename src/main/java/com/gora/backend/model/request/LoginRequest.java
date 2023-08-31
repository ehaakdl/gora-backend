package com.gora.backend.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
    public class LoginRequest{
        @JsonProperty("email")
        private String email;
        @JsonProperty("password")
        private String password;
    }