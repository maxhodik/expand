package com.example.hodik.expand.auth.exception;

public class UserNotFoundException extends RuntimeException {
    private final String email;

    public UserNotFoundException(String message, String email) {
        super(message);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
