package com.jose.redis.auth.auth

import com.jose.redis.auth.common.BaseControllerTest
import com.jose.redis.dto.request.SignInRequestDto
import com.jose.redis.enum.RoleType
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class SignInTest : BaseControllerTest() {
    @BeforeEach
    fun signUpUser() {
        signUpUser(1)
    }
    @Test
    @DisplayName("로그인 테스트(성공)")
    fun signInSuccess() {
        // Given
        val signInRequest = SignInRequestDto(testEmail, password)
        // When
        val test = mockMvc.post("/v1/signIn") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(signInRequest)
        }
        // Then
        test.andExpect {
            status { isOk() }
            assertNotNull(stringRedisTemplate.opsForValue().get("YOURSSU:$testEmail"))
            jsonPath("email") { value("testUser1@soongsil.ac.kr") }
            jsonPath("username") { value(username) }
            jsonPath("role") { RoleType.USER }
            jsonPath("accessToken") { exists() }
            jsonPath("refreshToken") { exists() }
        }

        stringRedisTemplate.delete("YOURSSU:testUser1@soongsil.ac.kr")
    }

    @Test
    @DisplayName("로그인 테스트(실패)-존재하지 않는 사용자")
    fun signInFailBecauseUserNotFound() {
        // Given
        val signInRequest = SignInRequestDto("testUser2@soongsil.ac.kr", password)
        // When
        val test = mockMvc.post("/v1/signIn") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(signInRequest)
        }
        // Then
        test.andExpect {
            status { isBadRequest() }
            jsonPath("errorCode") { value("유저서비스 오류 4번") }
        }
    }

    @Test
    @DisplayName("로그인 테스트(실패)-잘못된 비밀번호")
    fun signInFailBecausePasswordIsWrong() {
        // Given
        val signInRequest = SignInRequestDto(testEmail, "wrongPassword")
        // When
        val test = mockMvc.post("/v1/signIn") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(signInRequest)
        }
        // Then
        test.andExpect {
            status { isBadRequest() }
            jsonPath("errorCode") { value("유저서비스 오류 2번") }
        }
    }
}
