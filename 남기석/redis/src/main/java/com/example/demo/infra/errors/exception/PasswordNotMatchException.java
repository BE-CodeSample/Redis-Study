package com.example.demo.infra.errors.exception;

import lombok.Getter;

@Getter
public class PasswordNotMatchException extends RuntimeException{
    private final int errorCode;

    public PasswordNotMatchException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
