package com.jose.redis.controller

import com.jose.redis.entity.Person
import com.jose.redis.repository.PersonRedisRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RedisTestController(
    private val personRedisRepository: PersonRedisRepository
) {
    @GetMapping("/person")
    fun getMemberList(): ResponseEntity<Any> {
        val users = personRedisRepository.findAll()

        val data = HashMap<String, Any>()
        data["personList"] = users
        return ResponseEntity.ok(data)
    }

    @PostMapping("/person")
    fun setMember(@RequestBody person: Person): ResponseEntity<Any> {
        personRedisRepository.save(person)

        val data = HashMap<String, Any>()
        data["stats"] = "success"
        return ResponseEntity.ok(data)
    }
}
