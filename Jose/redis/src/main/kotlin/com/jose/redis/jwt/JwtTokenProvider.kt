package com.jose.redis.jwt

import com.jose.redis.enum.RoleType
import com.jose.redis.exception.jwtException.InvalidTokenException
import com.jose.redis.security.TokenInfo
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.SignatureException
import io.jsonwebtoken.UnsupportedJwtException
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.Arrays
import java.util.Date
import java.util.stream.Collectors
import javax.naming.AuthenticationException

@Component
class JwtTokenProvider {
    private val ACCESS_TOKEN_VALID_TIME: Long = (1000 * 60 * 60 * 24) // 하루
    private val REFRESH_TOKEN_VALID_TIME: Long = (1000 * 60 * 60 * 24 * 7) // 일주일
    private val BEARER_TYPE = "Bearer "
    @Value("\${jwt.secret}")
    lateinit var secretKey: String

    fun createJwtAccessToken(email: String): TokenInfo {
        val now = Date()
        val expiration = Date(now.time + ACCESS_TOKEN_VALID_TIME)
        var accessToken: String = Jwts.builder()
            .claim("email", email)
            .claim("ROLE_", RoleType.USER)
            .setIssuedAt(now)
            .setExpiration(expiration)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
        accessToken = BEARER_TYPE + accessToken
        return TokenInfo(accessToken, expiration.time - now.time)
    }

    fun createJwtRefreshToken(email: String): TokenInfo {
        val now = Date()
        val expiration = Date(now.time + REFRESH_TOKEN_VALID_TIME)
        var refreshToken: String = Jwts.builder()
            .claim("email", email)
            .claim("ROLE_", RoleType.USER)
            .setIssuedAt(now)
            .setExpiration(expiration)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
        refreshToken = BEARER_TYPE + refreshToken
        return TokenInfo(refreshToken, expiration.time - now.time)
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            return true
        } catch (e: SecurityException) {
            throw InvalidTokenException("JWT에러 Security")
        } catch (e: MalformedJwtException) {
            throw InvalidTokenException("JWT에러 Malformed")
        } catch (e: ExpiredJwtException) {
            throw InvalidTokenException("만료된 JWT 토큰입니다.")
        } catch (e: UnsupportedJwtException) {
            throw InvalidTokenException("지원되지 않는 JWT 토큰입니다.")
        } catch (e: IllegalArgumentException) {
            throw InvalidTokenException("잘못된 JWT 헤더 입니다.")
        } catch (e: SignatureException) {
            throw InvalidTokenException("잘못된 JWT 서명 입니다.")
        }
    }

    fun getAuthentication(token: String): Authentication {
        val claims = parseClaims(token)
        try {
            claims.get("email")
        } catch (e: Exception) {
            throw AuthenticationException("JWT 토큰에 email이 없습니다.")
        }
        val authorities: Collection<GrantedAuthority> =
            Arrays.stream(claims.get("ROLE_").toString().split(",").toTypedArray())
                .map { role: String -> SimpleGrantedAuthority(role) }
                .collect(Collectors.toList())
        val userDetails: UserDetails = User(claims.get("email").toString(), "", authorities)
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun parseClaims(token: String?): Claims {
        return try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }

    fun getExpiration(token: String): Long {
        val expiration: Date =
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(resolveToken(token)).body.expiration
        val now = Date()
        return expiration.time - now.time
    }

    fun resolveToken(token: String): String? {
        if (token.startsWith(BEARER_TYPE)) {
            return token.replace("Bearer ", "")
        }
        return null
    }
}
