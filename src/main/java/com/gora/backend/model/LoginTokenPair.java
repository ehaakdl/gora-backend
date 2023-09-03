package com.gora.backend.model;

import lombok.Value;

@Value
public class LoginTokenPair {
    String access;
    String refresh;
}