package com.jose.redis.common

import com.jose.redis.repository.PersonRedisRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RedisRepositoryBaseTest {
    @Autowired
    lateinit var personRedisRepository: PersonRedisRepository
}
