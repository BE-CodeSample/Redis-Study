package com.example.demo.infra.errors.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException{
    private final int errorCode;

    public UserNotFoundException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
