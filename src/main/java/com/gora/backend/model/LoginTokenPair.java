package com.gora.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.thymeleaf.model.IProcessingInstruction;

import java.util.Objects;

@Value
public class LoginTokenPair {
    String access;
    String refresh;
}