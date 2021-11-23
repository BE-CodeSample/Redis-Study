package com.jose.redis.redisTemplate.dataStructure

import com.jose.redis.redisTemplate.common.RedisTemplateBaseTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ListTest : RedisTemplateBaseTest() {
    val key = "list"
    @BeforeEach
    fun deleteList() {
        stringRedisTemplate.delete(key)
    }
    @Test
    fun listTest() {
        val stringStringListOperations = stringRedisTemplate.opsForList()

        // rightPush는 오른쪽에서 푸시한것으로 생각하면 좋다.
        stringStringListOperations.rightPush(key, "H")
        stringStringListOperations.rightPush(key, "e")
        stringStringListOperations.rightPush(key, "l")
        stringStringListOperations.rightPush(key, "l")
        stringStringListOperations.rightPush(key, "o")
        stringStringListOperations.rightPushAll(key, " ", "W", "o", "r", "l", "d")

        val result1 = stringStringListOperations.index(key, 1)
        assertEquals(result1, "e")

        val result2 = stringStringListOperations.size(key)
        assertEquals(result2, 11)

        val resultList = stringStringListOperations.range(key, 0, -1)!!
        assertEquals(resultList[0], "H")
        assertEquals(resultList[1], "e")
        assertEquals(resultList[2], "l")
        assertEquals(resultList[3], "l")
        assertEquals(resultList[4], "o")
        assertEquals(resultList[5], " ")
        assertEquals(resultList[6], "W")
        assertEquals(resultList[7], "o")
        assertEquals(resultList[8], "r")
        assertEquals(resultList[9], "l")
        assertEquals(resultList[10], "d")
        // 잘 저장되었는지 레디스에서 lrange list 0 -1 로 확인한다.
    }
}
