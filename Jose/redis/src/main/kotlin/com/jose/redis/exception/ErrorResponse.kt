package com.jose.redis.exception

import java.time.LocalDateTime

class ErrorResponse(
    val timestamp: LocalDateTime,
    val httpStatus: Int,
    val errorCode: String,
    val message: String,
    val path: String
)
