package com.jose.redis.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("people")
class Person(
    firstName: String,
    lastName: String,
    address: Address
) {
    @Id
    lateinit var id: String
    val firstName = firstName
    val lastName = lastName
    val address = address
}
