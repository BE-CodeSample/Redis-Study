package com.jose.redis.exception

import com.jose.redis.exception.userServiceException.ExistsEmailException
import com.jose.redis.exception.userServiceException.PasswordIncorrectException
import com.jose.redis.exception.userServiceException.RefreshTokenImproperUseException
import com.jose.redis.exception.userServiceException.RefreshTokenNotFoundException
import com.jose.redis.exception.userServiceException.UserNotFoundException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@RestControllerAdvice
class UserServiceExceptionHandler {
    private val log = KotlinLogging.logger {}
    @ExceptionHandler(ExistsEmailException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidStationNameException(exception: ExistsEmailException, request: HttpServletRequest): ErrorResponse {
        log.warn { "요청 URI : " + request.requestURI + " 에러 메시지 : " + exception.message }
        return ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "유저서비스 오류 1번", exception.message!!, request.requestURI)
    }

    @ExceptionHandler(PasswordIncorrectException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handlePasswordInCorrectException(exception: PasswordIncorrectException, request: HttpServletRequest): ErrorResponse {
        log.warn { "요청 URI : " + request.requestURI + " 에러 메시지 : " + exception.message }
        return ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "유저서비스 오류 2번", exception.message!!, request.requestURI)
    }

    @ExceptionHandler(RefreshTokenNotFoundException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleRefreshTokenNotFoundException(exception: RefreshTokenNotFoundException, request: HttpServletRequest): ErrorResponse {
        log.warn { "요청 URI : " + request.requestURI + " 에러 메시지 : " + exception.message }
        return ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "유저서비스 오류 3번", exception.message!!, request.requestURI)
    }

    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleUserNotFoundException(exception: UserNotFoundException, request: HttpServletRequest): ErrorResponse {
        log.warn { "요청 URI : " + request.requestURI + " 에러 메시지 : " + exception.message }
        return ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "유저서비스 오류 4번", exception.message!!, request.requestURI)
    }

    @ExceptionHandler(RefreshTokenImproperUseException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleRefreshTokenImproperUseException(exception: RefreshTokenImproperUseException, request: HttpServletRequest): ErrorResponse {
        log.warn { "요청 URI : " + request.requestURI + " 에러 메시지 : " + exception.message }
        return ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "유저서비스 오류 5번", exception.message!!, request.requestURI)
    }
}
