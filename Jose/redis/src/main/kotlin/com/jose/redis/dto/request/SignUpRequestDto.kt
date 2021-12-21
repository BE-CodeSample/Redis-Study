package com.jose.redis.dto.request

import com.jose.redis.enum.RoleType
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class SignUpRequestDto(
    @field:Email
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val username: String,
    @field:NotBlank
    val password: String,
    val role: RoleType
)
