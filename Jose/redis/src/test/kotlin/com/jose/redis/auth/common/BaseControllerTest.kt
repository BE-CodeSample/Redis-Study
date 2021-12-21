package com.jose.redis.auth.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.jose.redis.dto.response.SignUpResponseDto
import com.jose.redis.enum.RoleType
import com.jose.redis.jwt.JwtTokenProvider
import com.jose.redis.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class BaseControllerTest {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    protected lateinit var userService: UserService

    @Autowired
    protected lateinit var stringRedisTemplate: StringRedisTemplate

    val email = "testmail@soongsil.ac.kr"
    val username = "username1234"
    val password = "test1234"
    val AUTHORIZATION_HEADER = "Authorization"

    lateinit var testEmail: String
    lateinit var accessToken: String
    lateinit var refreshToken: String

    protected fun signUpUser(id: Int): SignUpResponseDto {
        testEmail = "testUser$id@soongsil.ac.kr"
        return userService.signUp(testEmail, username, password, RoleType.USER)
    }

    protected fun signInUser(id: Int) {
        testEmail = "testUser$id@soongsil.ac.kr"
        val signInResponse = userService.signIn(testEmail, password)
        accessToken = signInResponse.accessToken
        refreshToken = signInResponse.refreshToken
    }
}
