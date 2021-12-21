package com.jose.redis.auth.auth

import com.jose.redis.auth.common.BaseControllerTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.post
import java.util.concurrent.TimeUnit

class ReissueTest : BaseControllerTest() {
    @BeforeEach
    fun signUpUserAndSignInUser() {
        signUpUser(1)
        signInUser(1)
    }

    @AfterEach
    fun deleteToken() {
        stringRedisTemplate.delete("YOURSSU:$testEmail")
    }

    @Test
    @DisplayName("토큰 재발급 테스트(성공)")
    fun reissueSuccess() {
        // When
        val test = mockMvc.post("/v1/reissue") {
            header(AUTHORIZATION_HEADER, refreshToken)
        }
        // Then
        test.andExpect {
            status { isOk() }
            assertNotNull(stringRedisTemplate.opsForValue().get("YOURSSU:$testEmail"))
            jsonPath("email") { value(testEmail) }
            jsonPath("accessToken") { exists() }
            jsonPath("refreshToken") { exists() }
            assertEquals(stringRedisTemplate.opsForValue().get("BLACKLIST:$refreshToken"), "reissue")
        }
        stringRedisTemplate.delete("BLACKLIST:$refreshToken")
    }

    @Test
    @DisplayName("토큰 재발급 테스트(실패)-유저가 없는 경우")
    fun reissueFailBecauseNotFoundUser() {
        // Given
        val refreshTokenInfo = jwtTokenProvider.createJwtRefreshToken("testUser2@soongsil.ac.kr")
        // When
        val test = mockMvc.post("/v1/reissue") {
            header(AUTHORIZATION_HEADER, refreshTokenInfo.token)
        }
        // Then
        test.andExpect {
            status { isBadRequest() }
            jsonPath("errorCode") { value("유저서비스 오류 4번") }
        }
    }

    @Test
    @DisplayName("토큰 재발급 테스트(실패)-리프레시 토큰을 찾을수 없는경우(유효한 액세스토큰을 주는 경우)")
    fun reissueFailBecauseNotFoundRefreshToken() {
        // When
        val test = mockMvc.post("/v1/reissue") {
            header(AUTHORIZATION_HEADER, accessToken)
        }
        // Then
        test.andExpect {
            status { isBadRequest() }
            jsonPath("errorCode") { value("유저서비스 오류 3번") }
        }
    }

    @Test
    @DisplayName("토큰 재발급 테스트(실패)-블랙리스트 액세스토큰을 주는 경우")
    fun reissueFailBecauseBlackListAccessToken() {
        // Given
        stringRedisTemplate.opsForValue()
            .set("BLACKLIST:$accessToken", "reissue", jwtTokenProvider.getExpiration(accessToken), TimeUnit.MILLISECONDS)
        // When
        val test = mockMvc.post("/v1/reissue") {
            header(AUTHORIZATION_HEADER, accessToken)
        }
        // Then
        test.andExpect {
            status { isBadRequest() }
            jsonPath("errorCode") { value("토큰 오류 1번") }
        }
        stringRedisTemplate.delete("BLACKLIST:$accessToken")
    }

    @Test
    @DisplayName("토큰 재발급 테스트(실패)-블랙리스트 리프레시 토큰을 주는 경우")
    fun reissueFailBecauseBlackListRefreshToken() {
        // Given
        stringRedisTemplate.opsForValue()
            .set("BLACKLIST:$refreshToken", "reissue", jwtTokenProvider.getExpiration(refreshToken), TimeUnit.MILLISECONDS)
        // When
        val test = mockMvc.post("/v1/reissue") {
            header(AUTHORIZATION_HEADER, refreshToken)
        }
        // Then
        test.andExpect {
            status { isBadRequest() }
            jsonPath("errorCode") { value("토큰 오류 1번") }
        }
        stringRedisTemplate.delete("BLACKLIST:$refreshToken")
    }

    @Test
    @DisplayName("토큰 재발급 테스트(실패)-토큰이 이상한 경우")
    fun reissueFailBecauseWrongToken() {
        // When
        val test = mockMvc.post("/v1/reissue") {
            header(AUTHORIZATION_HEADER, "wrongToken")
        }
        // Then
        test.andExpect {
            status { isBadRequest() }
            jsonPath("errorCode") { value("토큰 오류 1번") }
        }
    }

    @Test
    @DisplayName("토큰 재발급 테스트(실패)-헤더 값이 없는 경우")
    fun reissueFailBecauseNotHeader() {
        // When
        val test = mockMvc.post("/v1/reissue") {
        }
        // Then
        test.andExpect {
            status { isBadRequest() }
            jsonPath("errorCode") { value("토큰 오류 2번") }
        }
    }
}
