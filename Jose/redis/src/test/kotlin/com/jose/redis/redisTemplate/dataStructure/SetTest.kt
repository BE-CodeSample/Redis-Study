package com.jose.redis.redisTemplate.dataStructure

import com.jose.redis.redisTemplate.common.RedisTemplateBaseTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SetTest : RedisTemplateBaseTest() {
    val key = "set"
    @BeforeEach
    fun deleteSet() {
        stringRedisTemplate.delete(key)
    }
    @Test
    fun setTest() {
        val stringStringSetOperations = stringRedisTemplate.opsForSet()

        stringStringSetOperations.add(key, "H")
        stringStringSetOperations.add(key, "e")
        stringStringSetOperations.add(key, "l")
        stringStringSetOperations.add(key, "l")
        stringStringSetOperations.add(key, "o")

        val result1 = stringStringSetOperations.size(key)
        assertEquals(result1, 4)
        // 잘 저장되었는지 레디스에서 smembers set 으로 확인한다.
    }
}
