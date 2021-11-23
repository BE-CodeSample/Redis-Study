package com.example.demo.module.dto;

import lombok.Data;


// 토큰 갱신을 위해서는 accessToken, refreshToken 값이 필요
@Data
public class Reissue {
    private String accessToken;
    private String refreshToken;
}
