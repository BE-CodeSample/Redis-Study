package com.jose.redis.exception

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@RestControllerAdvice
class GeneralExceptionHandler {
    private val log = KotlinLogging.logger {}
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(exception: MethodArgumentNotValidException, request: HttpServletRequest): ErrorResponse {
        log.warn { "요청 URI : " + request.requestURI + " 에러 메시지 : " + exception.bindingResult.allErrors[0].defaultMessage }
        return ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "시스템 오류 1번", exception.bindingResult.allErrors[0].defaultMessage!!, request.requestURI)
    }
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(exception: HttpMediaTypeNotSupportedException, request: HttpServletRequest): ErrorResponse {
        log.warn { "요청 URI : " + request.requestURI + " 에러 메시지 : " + exception.message }
        return ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "시스템 오류 2번", exception.message!!, request.requestURI)
    }
}
