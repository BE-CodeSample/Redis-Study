package com.example.demo.infra.jwt;

import com.example.demo.infra.errors.ErrorCode;
import com.example.demo.infra.errors.exception.LogOutedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider provider;
    private final RedisTemplate redisTemplate;


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String token = provider.getToken(request);
        if (token != null && provider.validateToken(token)) {

            // logout된 accessToken인지 검증
            // logout된 accessToken은 redis에 저장되어있을 것
            String isLogout = (String) redisTemplate.opsForValue().get(token);
            if (!ObjectUtils.isEmpty(isLogout)) {
                throw new LogOutedException(ErrorCode.LOGOUT_ACCESS_TOKEN.getErrorMessage(),
                        ErrorCode.LOGOUT_ACCESS_TOKEN.getErrorCode());
            }
            Authentication auth = provider.getAuth(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(req,res);
    }
}
