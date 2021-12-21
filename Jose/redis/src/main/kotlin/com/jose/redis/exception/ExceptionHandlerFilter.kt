package com.jose.redis.exception

import com.fasterxml.jackson.databind.ObjectMapper
import com.jose.redis.exception.jwtException.HeaderHasNotAuthorization
import com.jose.redis.exception.jwtException.InvalidTokenException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.time.LocalDateTime
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ExceptionHandlerFilter(private val objectMapper: ObjectMapper) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (exception: InvalidTokenException) {
            return setErrorResponse(
                HttpStatus.BAD_REQUEST,
                request, response, exception.message!!, "토큰 오류 1번"
            )
        } catch (exception: HeaderHasNotAuthorization) {
            return setErrorResponse(
                HttpStatus.BAD_REQUEST,
                request, response, exception.message!!, "토큰 오류 2번"
            )
        }
    }

    fun setErrorResponse(status: HttpStatus, request: HttpServletRequest, response: HttpServletResponse, exceptionMessage: String, errorCode: String) {
        response.status = status.value()
        response.contentType = "application/json"
        val errorResponse =
            ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), errorCode, exceptionMessage, request.requestURI)
        try {
            response.characterEncoding = "UTF-8"
            response.writer.write(objectMapper.writeValueAsString(errorResponse))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
