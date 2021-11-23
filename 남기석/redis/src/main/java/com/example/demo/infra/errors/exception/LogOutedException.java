package com.example.demo.infra.errors.exception;

import lombok.Getter;

@Getter
public class LogOutedException extends RuntimeException {
    private final int errorCode;

    public LogOutedException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
