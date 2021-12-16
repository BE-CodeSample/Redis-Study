package com.jose.redis.redisTemplate.dataStructure

import com.jose.redis.redisTemplate.common.RedisTemplateBaseTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SortedSetTest : RedisTemplateBaseTest() {
    val key = "sortedSet"
    @BeforeEach
    fun deleteSortedSet() {
        stringRedisTemplate.delete(key)
    }
    @Test
    fun sortedSetTest() {
        val stringStringZSetOperations = stringRedisTemplate.opsForZSet()

        stringStringZSetOperations.add(key, "H", 1.0)
        stringStringZSetOperations.add(key, "e", 5.0)
        stringStringZSetOperations.add(key, "l", 3.0)
        stringStringZSetOperations.add(key, "l", 100.0)
        stringStringZSetOperations.add(key, "o", 50.0)

        val result1 = stringStringZSetOperations.range(key, 0, -1)!!
        val result1Iterator = result1.iterator()
        val compare = listOf("H", "e", "o", "l")
        val compareIterator = compare.iterator()
        while (result1Iterator.hasNext() && compareIterator.hasNext()) {
            assertEquals(result1Iterator.next(), compareIterator.next())
        }

        val result2 = stringStringZSetOperations.size(key)
        assertEquals(result2, 4)
        // 잘 저장되었는지 레디스에서 zrange sortedSet 0 -1 으로 확인한다.
    }
}
