package com.gora.backend.model.entity;

public enum eSocialType {
    google;

    public static eSocialType convert(String email) {
        if (email.contains("gmail.com")) {
            return eSocialType.google;
        } else {
            return null;
        }
    }
}
