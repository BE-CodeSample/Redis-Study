package com.example.demo.infra.errors.exception;

import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {
    private final int errorCode;

    public AuthenticationException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
