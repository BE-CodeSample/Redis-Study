package com.jose.redis.service

import com.jose.redis.dto.response.ReissueResponseDto
import com.jose.redis.dto.response.SignInResponseDto
import com.jose.redis.dto.response.SignUpResponseDto
import com.jose.redis.entity.UserInfo
import com.jose.redis.enum.RoleType
import com.jose.redis.exception.userServiceException.ExistsEmailException
import com.jose.redis.exception.userServiceException.PasswordIncorrectException
import com.jose.redis.exception.userServiceException.RefreshTokenImproperUseException
import com.jose.redis.exception.userServiceException.RefreshTokenNotFoundException
import com.jose.redis.exception.userServiceException.UserNotFoundException
import com.jose.redis.jwt.JwtTokenProvider
import com.jose.redis.repository.UserRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val bCryptPasswordEncoder: PasswordEncoder,
    private val stringRedisTemplate: StringRedisTemplate
) {
    @Transactional
    fun signUp(email: String, username: String, password: String, role: RoleType): SignUpResponseDto {
        if (userRepository.existsByEmail(email)) {
            throw ExistsEmailException("이미 가입한 이메일 입니다.")
        }
        userRepository.save(
            UserInfo(
                email = email,
                password = bCryptPasswordEncoder.encode(password),
                username = username,
                role = RoleType.USER,
            )
        )
        return SignUpResponseDto(email, username, role)
    }

    @Transactional
    fun signIn(email: String, password: String): SignInResponseDto {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException("사용자를 찾을 수 없습니다.")
        if (!bCryptPasswordEncoder.matches(password, user.password)) {
            throw PasswordIncorrectException("비밀번호가 맞지 않습니다.")
        }
        val accessTokenDto = jwtTokenProvider.createJwtAccessToken(email)
        val refreshTokenDto = jwtTokenProvider.createJwtRefreshToken(email)
        stringRedisTemplate.opsForValue().set(
            "YOURSSU:$email", refreshTokenDto.token, refreshTokenDto.expirationTime, TimeUnit.MILLISECONDS
        )
        return SignInResponseDto(user.email, user.username, "USER", accessTokenDto.token, refreshTokenDto.token)
    }

    @Transactional
    fun reissue(refreshToken: String, email: String): ReissueResponseDto {
        if (!userRepository.existsByEmail(email)) {
            throw UserNotFoundException("사용자를 찾을 수 없습니다.")
        }
        if (stringRedisTemplate.opsForValue().get("YOURSSU:$email") != refreshToken) {
            throw RefreshTokenNotFoundException("리프레시 토큰을 찾을 수 없습니다. 다시 로그인 하세요.")
        }
        val accessTokenDto = jwtTokenProvider.createJwtAccessToken(email)
        val refreshTokenDto = jwtTokenProvider.createJwtRefreshToken(email)
        stringRedisTemplate.opsForValue()
            .set("BLACKLIST:$refreshToken", "reissue", jwtTokenProvider.getExpiration(refreshToken), TimeUnit.MILLISECONDS)
        stringRedisTemplate.opsForValue()
            .set("YOURSSU:$email", refreshTokenDto.token, refreshTokenDto.expirationTime, TimeUnit.MILLISECONDS)
        return ReissueResponseDto(email, accessTokenDto.token, refreshTokenDto.token)
    }

    @Transactional
    fun logout(accessToken: String, email: String) {
        if (!userRepository.existsByEmail(email)) {
            throw UserNotFoundException("사용자를 찾을 수 없습니다.")
        }
        // 현재 가지고 있는 refreshToken을 찾는다.
        // refreshToken이 서버에 없어도 일단은 로그아웃 되게 했다.
        val refreshToken = stringRedisTemplate.opsForValue().get("YOURSSU:$email")
        if (accessToken == refreshToken) {
            throw RefreshTokenImproperUseException("액세스 토큰으로 다시 요청하거나 액세스 토큰이 없다면 해당 토큰으로 재발급 요청을 한 후 다시 시도해 주세요.")
        }
        if (refreshToken != null) {
            stringRedisTemplate.delete("YOURSSU:$email")
            stringRedisTemplate.opsForValue()
                .set("BLACKLIST:$refreshToken", "logout", jwtTokenProvider.getExpiration(refreshToken), TimeUnit.MILLISECONDS)
        }
        stringRedisTemplate.opsForValue()
            .set("BLACKLIST:$accessToken", "logout", jwtTokenProvider.getExpiration(accessToken), TimeUnit.MILLISECONDS)
    }

    @Transactional
    fun withdraw(accessToken: String, email: String) {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException("사용자를 찾을 수 없습니다.")
        userRepository.delete(user)
        // 현재 가지고 있는 refreshToken을 찾는다.
        // refreshToken이 서버에 없어도 일단은 회원탈퇴 되게 했다.
        val refreshToken = stringRedisTemplate.opsForValue().get("YOURSSU:$email")
        if (accessToken == refreshToken) {
            throw RefreshTokenImproperUseException("액세스 토큰으로 다시 요청하거나 액세스 토큰이 없다면 해당 토큰으로 재발급 요청을 한 후 다시 시도해 주세요.")
        }
        if (refreshToken != null) {
            stringRedisTemplate.delete("YOURSSU:$email")
            stringRedisTemplate.opsForValue()
                .set("BLACKLIST:$refreshToken", "withdraw", jwtTokenProvider.getExpiration(refreshToken), TimeUnit.MILLISECONDS)
        }
        stringRedisTemplate.opsForValue()
            .set("BLACKLIST:$accessToken", "withdraw", jwtTokenProvider.getExpiration(accessToken), TimeUnit.MILLISECONDS)
    }
}
