package com.jose.redis.redisTemplate.dataStructure

import com.jose.redis.redisTemplate.common.RedisTemplateBaseTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StringTest : RedisTemplateBaseTest() {
    val key = "string"
    @BeforeEach
    fun deleteList() {
        stringRedisTemplate.delete(key)
    }
    @Test
    fun testStrings() {
        val stringStringValueOperations = stringRedisTemplate.opsForValue()
        // redis set 명령어
        stringStringValueOperations.set(key, "abc")
        // redis get 명령어
        val result = stringStringValueOperations.get(key)
        assertEquals(result, "abc")
        // 실행하고 레디스에 들어가서 get string 을 입력해보자
        // "\"abc\"" 이렇게 나온다. 바이트코드가 약간 섞인다.
        // 레디스의 저장형식은 byte array이기 때문에 그런거 같다.(확실하지 않음)
    }
}
