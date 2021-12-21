package com.jose.redis.dto.response

import com.jose.redis.enum.RoleType

class SignUpResponseDto(
    val email: String,
    val username: String,
    val role: RoleType
)
