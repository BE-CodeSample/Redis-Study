package com.jose.redis.redisRepository

import com.jose.redis.common.RedisRepositoryBaseTest
import com.jose.redis.entity.Address
import com.jose.redis.entity.Person
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.Boolean

class RedisRepositoryTest : RedisRepositoryBaseTest() {
    @BeforeEach
    fun deletePerson() {
        personRedisRepository.deleteAll()
    }
    @Test
    fun savePerson() {
        // given
        val address = Address("대한민국", "서울특별시")
        val person = Person("firstName", "lastName", address)

        // when
        val savedPerson: Person = personRedisRepository.save(person)

        // then
        val findPerson = personRedisRepository.findById(savedPerson.id!!)
        assertThat(findPerson.isPresent).isEqualTo(Boolean.TRUE)
        assertThat(findPerson.get().firstName).isEqualTo(person.firstName)
        // 잘 저장되었는지 keys * 로 나온 people:hash값 을 복사한다.
        // hgetall people:hash값을 한다.
        // hkeys people:a16f9219-dbc3-46de-9c1e-cb9d3adb0f55 로 키를 확인한다.
        // hkeys를 하면 person객체 안의 내부 객체는 address.country와 address.city로 표현된다.
        // hvals people:a16f9219-dbc3-46de-9c1e-cb9d3adb0f55 로 값을 확인한다.
        // hvals를 하면 person객체 안의 내부 객체는 이상한 값으로 표현된다.
    }
}
