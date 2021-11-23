package com.example.demo.infra.errors;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(1001,"해당 유저는 존재하지 않습니다."),
    PASSWORD_NOT_MATCH(1002,"패스워드가 틀렸습니다."),
    Authenticate_INVALID_Exception(2001,"토큰 정보가 유효하지 않습니다"),
    LOGOUT_ACCESS_TOKEN(2002,"로그아웃 된 토큰입니다.");



    private int errorCode;
    private String errorMessage;

    ErrorCode(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
