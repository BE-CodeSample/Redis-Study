package com.jose.redis.redisTemplate.common

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate

@SpringBootTest
class RedisTemplateBaseTest {
    @Autowired
    lateinit var stringRedisTemplate: StringRedisTemplate
}
