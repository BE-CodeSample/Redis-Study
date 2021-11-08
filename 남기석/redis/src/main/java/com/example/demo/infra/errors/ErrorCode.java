package com.example.demo.infra.errors;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(1001,"해당 유저는 존재하지 않습니다."),
    PASSWORD_NOT_MATCH(1002,"패스워드가 틀렸습니다."),
    Authenticate_INVALID_Exception(2001,"Invalid JWT Token"),
    Authenticate_EXPIRED_Exception(2002,"Expired JWT Token"),
    Authenticate_UNSUPPORTED_Exception(2003,"Unsupported JWT Token"),
    Authenticate_EMPTY_Exception(2004, "JWT claims string is empty.");


    private int errorCode;
    private String errorMessage;

    ErrorCode(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
