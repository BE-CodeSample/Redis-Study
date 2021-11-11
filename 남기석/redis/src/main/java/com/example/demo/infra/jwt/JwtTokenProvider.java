package com.example.demo.infra.jwt;



import com.example.demo.module.service.AccountService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.example.demo.module.dto.response.UserResponse;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;              // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;    // 7일
    private final AccountService accountService;


    private String secretKey = "secretKey";
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    // 인증정보 기반으로 토큰 생성
    public UserResponse.TokenInfo generateToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        Date date = new Date();
        // accessToken 생성
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();

        //refresh Token생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();

        return UserResponse.TokenInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }

    // 토큰 인증정보 조회
    public Authentication getAuth(String token) {
        //token에서 username정보 꺼낸 후
        UserDetails userDetails = accountService.loadUserByUsername(getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // 요청 헤더 중 X-AUTH-TOKEN헤더에서 토큰 가져온다
    public String getToken(HttpServletRequest req) {
        return req.getHeader("X-AUTH-TOKEN");
    }


    //토큰 유효성검사
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
//            if (logoutList.contains(token)) {
//                return false;
//            }
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}

