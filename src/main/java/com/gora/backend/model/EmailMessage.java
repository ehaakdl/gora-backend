package com.gora.backend.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailMessage {

    private String to;
    private String subject;
    private String message;
    public static EmailMessage create(String email, String emailVerifyUrl, String subject) {
        return EmailMessage.builder().message(emailVerifyUrl).subject(subject).to(email).build();
    }
}