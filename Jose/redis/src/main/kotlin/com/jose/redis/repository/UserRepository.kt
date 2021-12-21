package com.jose.redis.repository

import com.jose.redis.entity.UserInfo
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserInfo, Int> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): UserInfo?
}
