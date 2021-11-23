package com.jose.redis.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(
    @Value("\${spring.redis.host}")
    private val redisHost: String,
    @Value("\${spring.redis.port}")
    private val redisPort: Int
) {
    @Bean
    fun redisConnectionFactory() = LettuceConnectionFactory(redisHost, redisPort)

    @Bean
    fun redisTemplate(): StringRedisTemplate {
        val stringRedisTemplate = StringRedisTemplate()
//        return stringRedisTemplate.apply {
//            //코틀린에서 추천하는대로 바꾸면 val이기때문에 다시 할당할수 없다고 나온다.
        // 해당 클래스의 모든 변수를 var로 바꿔도 똑같은 오류가 발생한다.
//            setConnectionFactory(redisConnectionFactory())
//            keySerializer = StringRedisSerializer()
//            valueSerializer = GenericJackson2JsonRedisSerializer() }

        // 위의 apply를 다음과 같이 바꿀수 있다.
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory())
        stringRedisTemplate.keySerializer = StringRedisSerializer()
        stringRedisTemplate.valueSerializer = GenericJackson2JsonRedisSerializer()
        return stringRedisTemplate
    }

//    @Bean
    // RedisTemplate<String,Any>의 뜻은 키는 String 이고 value 는 아무거나 가능하다 라는 뜻이다.
    // 이것을 사용해도 좋지만 우리는 긴 문자열인 jwt토큰을 사용할것이기 때문에 StringRedisTemplate 을 사용하겠다.
//    fun redisTemplate(): RedisTemplate<String, Any> {
//        val redisTemplate = RedisTemplate<String, Any>()
//        return redisTemplate.apply {
//            //코틀린에서 추천하는대로 바꾸면 redisPort가 val이기때문에 다시 할당할수 없다고 나온다.
//            setConnectionFactory(redisConnectionFactory())
//            keySerializer = StringRedisSerializer()
//            valueSerializer = GenericJackson2JsonRedisSerializer() }
//    }
}
