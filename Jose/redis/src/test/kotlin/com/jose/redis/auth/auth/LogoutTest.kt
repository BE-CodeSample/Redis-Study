package com.jose.redis.auth.auth

import com.jose.redis.auth.common.BaseControllerTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.post
import java.util.concurrent.TimeUnit

class LogoutTest : BaseControllerTest() {

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
    @DisplayName("로그아웃 테스트(성공)-리프레시 토큰이 서버에 있는 경우")
    fun logoutSuccessWithRefreshToken() {
        // When
        val test = mockMvc.post("/v1/logout") {
            header(AUTHORIZATION_HEADER, accessToken)
        }
        // Then
        test.andExpect {
            status { isOk() }
            assertNull(stringRedisTemplate.opsForValue().get("YOURSSU:$testEmail"))
            assertNotNull(stringRedisTemplate.opsForValue().get("BLACKLIST:$accessToken"))
            assertNotNull(stringRedisTemplate.opsForValue().get("BLACKLIST:$refreshToken"))
            assertEquals(stringRedisTemplate.opsForValue().get("BLACKLIST:$accessToken"), "logout")
            assertEquals(stringRedisTemplate.opsForValue().get("BLACKLIST:$refreshToken"), "logout")
        }
        stringRedisTemplate.delete("BLACKLIST:$accessToken")
        stringRedisTemplate.delete("BLACKLIST:$refreshToken")
    }

    @Test
    @DisplayName("로그아웃 테스트(성공)-리프레시 토큰이 서버에 없는 경우")
    fun logoutSuccessWithoutRefreshToken() {
        // Given
        stringRedisTemplate.delete("YOURSSU:$testEmail")
        // When
        val test = mockMvc.post("/v1/logout") {
            header(AUTHORIZATION_HEADER, accessToken)
        }
        // Then
        test.andExpect {
            status { isOk() }
            assertNotNull(stringRedisTemplate.opsForValue().get("BLACKLIST:$accessToken"))
            assertNull(stringRedisTemplate.opsForValue().get("BLACKLIST:$refreshToken"))
            assertEquals(stringRedisTemplate.opsForValue().get("BLACKLIST:$accessToken"), "logout")
        }
        stringRedisTemplate.delete("BLACKLIST:$accessToken")
    }

    @Test
    @DisplayName("로그아웃 테스트(실패)-리프레시 토큰을 사용한 경우")
    fun logoutFailBecauseRefreshTokenImproperUse() {
        // When
        val test = mockMvc.post("/v1/logout") {
            header(AUTHORIZATION_HEADER, refreshToken)
        }
        // Then
        test.andExpect {
            status { isBadRequest() }
            jsonPath("errorCode") { value("유저서비스 오류 5번") }
        }
    }

    @Test
    @DisplayName("로그아웃 테스트(실패)-유저가 없는 경우")
    fun logoutFailBecauseNotFoundUser() {
        // Given
        val accessTokenInfo = jwtTokenProvider.createJwtAccessToken("testUser2@soongsil.ac.kr")
        // When
        val test = mockMvc.post("/v1/logout") {
            header(AUTHORIZATION_HEADER, accessTokenInfo.token)
        }
        // Then
        test.andExpect {
            status { isBadRequest() }
            jsonPath("errorCode") { value("유저서비스 오류 4번") }
        }
    }

    @Test
    @DisplayName("로그아웃 테스트(실패)-블랙리스트 액세스토큰을 주는 경우")
    fun logoutFailBecauseBlackListAccessToken() {
        // Given
        stringRedisTemplate.opsForValue()
            .set("BLACKLIST:$accessToken", "logout", jwtTokenProvider.getExpiration(accessToken), TimeUnit.MILLISECONDS)
        // When
        val test = mockMvc.post("/v1/logout") {
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
    @DisplayName("로그아웃 테스트(실패)-블랙리스트 리프레시 토큰을 주는 경우")
    fun logoutFailBecauseBlackListRefreshToken() {
        // Given
        stringRedisTemplate.opsForValue()
            .set("BLACKLIST:$refreshToken", "logout", jwtTokenProvider.getExpiration(refreshToken), TimeUnit.MILLISECONDS)
        // When
        val test = mockMvc.post("/v1/logout") {
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
    @DisplayName("로그아웃 테스트(실패)-토큰이 이상한 경우")
    fun logoutFailBecauseWrongToken() {
        // When
        val test = mockMvc.post("/v1/logout") {
            header(AUTHORIZATION_HEADER, "wrongToken")
        }
        // Then
        test.andExpect {
            status { isBadRequest() }
            jsonPath("errorCode") { value("토큰 오류 1번") }
        }
    }

    @Test
    @DisplayName("로그아웃 테스트(실패)-헤더 값이 없는 경우")
    fun logoutFailBecauseNotHeader() {
        // When
        val test = mockMvc.post("/v1/logout") {
        }
        // Then
        test.andExpect {
            status { isBadRequest() }
            jsonPath("errorCode") { value("토큰 오류 2번") }
        }
    }
}
