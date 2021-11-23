package com.example.demo.infra.config;

import com.example.demo.infra.errors.ErrorCode;
import com.example.demo.infra.errors.ErrorResponse;
import com.example.demo.infra.errors.exception.LogOutedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (LogOutedException e) {
            setErrorResponse(HttpStatus.BAD_REQUEST, response, e);
        }
    }

    private void setErrorResponse(HttpStatus status, HttpServletResponse response, LogOutedException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ErrorCode.LOGOUT_ACCESS_TOKEN.getErrorCode(),
                ErrorCode.LOGOUT_ACCESS_TOKEN.getErrorMessage());
        try {
            response.setStatus(status.value());
            response.setContentType("application/json");
            response.getWriter().write(convertObjectToJson(errorResponse));
        } catch (IOException exception) {
            e.printStackTrace();
        }
    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
