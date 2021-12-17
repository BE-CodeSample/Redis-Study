package com.jose.redis.repository

import com.jose.redis.entity.Person
import org.springframework.data.repository.CrudRepository

interface PersonRedisRepository : CrudRepository<Person, String>
