package com.example.demo.infra.errors;

import com.example.demo.infra.errors.exception.AuthenticationException;
import com.example.demo.infra.errors.exception.PasswordNotMatchException;
import com.example.demo.infra.errors.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    //todo exceptionHandler
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity handleUserNotFound() {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
                ErrorCode.USER_NOT_FOUND.getErrorCode(), ErrorCode.USER_NOT_FOUND.getErrorMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity handlePasswordNotMatch() {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
                ErrorCode.PASSWORD_NOT_MATCH.getErrorCode(), ErrorCode.PASSWORD_NOT_MATCH.getErrorMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity handleAuthFail() {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
                ErrorCode.Authenticate_INVALID_Exception.getErrorCode(), ErrorCode.Authenticate_INVALID_Exception.getErrorMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
