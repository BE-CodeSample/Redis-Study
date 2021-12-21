package com.jose.redis.auth.auth

import com.jose.redis.auth.common.BaseControllerTest
import com.jose.redis.dto.request.SignUpRequestDto
import com.jose.redis.enum.RoleType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class SignUpTest : BaseControllerTest() {
    @Test
    @DisplayName("회원가입 테스트(성공)")
    fun signUpSuccess() {
        // Given
        val signUpRequestDto = SignUpRequestDto(email, username, password, RoleType.USER)
        // When
        val test = mockMvc.post("/v1/signUp") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(signUpRequestDto)
        }
        // Then
        test.andExpect {
            status { isOk() }
            jsonPath("email") { value(email) }
            jsonPath("username") { value(username) }
            jsonPath("role") { value(RoleType.USER.toString()) }
        }
    }

    @Test
    @DisplayName("회원가입 테스트(실패)-잘못된 이메일")
    fun signUpFailBecauseInvalidEmail() {
        // Given
        val signUpRequestDto = SignUpRequestDto("email", username, password, RoleType.USER)
        // When
        val test = mockMvc.post("/v1/signUp") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(signUpRequestDto)
        }
        // Then
        test.andExpect {
            status { isBadRequest() }
            jsonPath("errorCode") { value("시스템 오류 1번") }
        }
    }

    @Test
    @DisplayName("회원가입 테스트(실패)-잘못된 비밀번호")
    fun signUpFailBecauseInvalidPassword() {
        // Given
        val signUpRequestDto = SignUpRequestDto(email, username, " ", RoleType.USER)
        // When
        val test = mockMvc.post("/v1/signUp") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(signUpRequestDto)
        }
        // Then
        test.andExpect {
            status { isBadRequest() }
            jsonPath("errorCode") { value("시스템 오류 1번") }
        }
    }

    @Test
    @DisplayName("회원가입 테스트(실패)-잘못된 유저네임")
    fun signUpFailBecauseInvalidUsername() {
        // Given
        val signUpRequestDto = SignUpRequestDto(email, " ", password, RoleType.USER)
        // When
        val test = mockMvc.post("/v1/signUp") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(signUpRequestDto)
        }
        // Then
        test.andExpect {
            status { isBadRequest() }
            jsonPath("errorCode") { value("시스템 오류 1번") }
        }
    }

    @Test
    @DisplayName("회원가입 테스트(실패)-지원하지 않는 미디어타입")
    fun signUpFailBecauseMediaTypeNotSupported() {
        // Given
        val signUpRequestDto = SignUpRequestDto("email", username, password, RoleType.USER)
        // When
        val test = mockMvc.post("/v1/signUp") {
            contentType = MediaType.TEXT_HTML
            content = objectMapper.writeValueAsString(signUpRequestDto)
        }
        // Then
        test.andExpect {
            status { isBadRequest() }
            jsonPath("errorCode") { value("시스템 오류 2번") }
        }
    }
}
