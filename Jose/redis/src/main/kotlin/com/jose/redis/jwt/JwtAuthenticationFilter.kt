package com.jose.redis.jwt

import com.jose.redis.exception.jwtException.HeaderHasNotAuthorization
import com.jose.redis.exception.jwtException.InvalidTokenException
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(
    private val tokenProvider: JwtTokenProvider,
    private val stringRedisTemplate: StringRedisTemplate
) : OncePerRequestFilter() {
    private val AUTHORIZATION_HEADER = "Authorization"
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)
            ?: throw HeaderHasNotAuthorization("Authorization 헤더가 요청에 없습니다.")
        val jwtToken = tokenProvider.resolveToken(bearerToken)
        if (jwtToken != null &&
            stringRedisTemplate.opsForValue().get("BLACKLIST:Bearer $jwtToken") == null &&
            tokenProvider.validateToken(jwtToken)
        ) {
            val authentication: Authentication = tokenProvider.getAuthentication(jwtToken)
            SecurityContextHolder.getContext().authentication = authentication
        } else {
            throw InvalidTokenException("토큰이 유효하지 않습니다.")
        }
        filterChain.doFilter(request, response)
    }
}
