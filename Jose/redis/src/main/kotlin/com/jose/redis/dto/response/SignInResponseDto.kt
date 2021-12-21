package com.jose.redis.dto.response

class SignInResponseDto(
    val email: String,
    val username: String,
    val role: String,
    val accessToken: String,
    val refreshToken: String
)
