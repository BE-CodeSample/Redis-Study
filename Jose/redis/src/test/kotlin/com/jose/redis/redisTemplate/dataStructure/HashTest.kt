package com.jose.redis.redisTemplate.dataStructure

import com.jose.redis.redisTemplate.common.RedisTemplateBaseTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HashTest : RedisTemplateBaseTest() {
    val key = "hash"
    @BeforeEach
    fun deleteHash() {
        stringRedisTemplate.delete(key)
    }
    @Test
    fun hashTest() {
        val stringStringHashOperations = stringRedisTemplate.opsForHash<String, String>()

        stringStringHashOperations.put(key, "hashKey1", "hashValue1")
        stringStringHashOperations.put(key, "hashKey2", "hashValue2")
        stringStringHashOperations.put(key, "hashKey3", "hashValue3")

        val result1 = stringStringHashOperations.get(key, "hashKey1")
        val result2 = stringStringHashOperations.get(key, "hashKey2")
        val result3 = stringStringHashOperations.get(key, "hashKey3")

        assertEquals(result1, "hashValue1")
        assertEquals(result2, "hashValue2")
        assertEquals(result3, "hashValue3")

        val entries = stringStringHashOperations.entries(key)

        assertEquals(entries["hashKey1"], "hashValue1")
        assertEquals(entries["hashKey2"], "hashValue2")
        assertEquals(entries["hashKey3"], "hashValue3")

        val size = stringStringHashOperations.size(key)
        assertEquals(size, 3)
        // 잘 저장되었는지 레디스에서 hgetAll hash로 확인한다.
        // 잘 저장되었는지 레디스에서 hkeys hash를 하면 hash라는 키 내부의 키들이 나온다.
        // 잘 저장되었는지 레이드에서 hvals hash를 하면 hash라는 키 내부의 값들이 나온다.
    }
}
