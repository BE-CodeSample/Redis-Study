package com.jose.redis.security

import com.jose.redis.enum.RoleType

class AuthInfo(val token: String, val email: String, val role: List<RoleType>)
