package com.example.demo.infra.errors;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private int errorCode;
    private String errorMessage;

    public ErrorResponse(HttpStatus httpStatus,int errorCode,String errorMessage) {
        this.timestamp = LocalDateTime.now();
        this.status = httpStatus.value();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
