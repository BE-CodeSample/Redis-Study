package com.jose.redis.controller

import com.jose.redis.dto.request.SignInRequestDto
import com.jose.redis.dto.request.SignUpRequestDto
import com.jose.redis.dto.response.ReissueResponseDto
import com.jose.redis.dto.response.SignInResponseDto
import com.jose.redis.dto.response.SignUpResponseDto
import com.jose.redis.security.AuthInfo
import com.jose.redis.security.Authenticated
import com.jose.redis.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class UserController(private val userService: UserService) {

    @PostMapping("/v1/signUp")
    fun signUp(@Valid @RequestBody signUpRequestDto: SignUpRequestDto): SignUpResponseDto {
        return userService.signUp(signUpRequestDto.email, signUpRequestDto.username, signUpRequestDto.password, signUpRequestDto.role)
    }

    @PostMapping("/v1/signIn")
    fun signIn(@RequestBody signInRequestDto: SignInRequestDto): SignInResponseDto {
        return userService.signIn(signInRequestDto.email, signInRequestDto.password)
    }

    @PostMapping("/v1/reissue")
    fun reissue(@Authenticated authInfo: AuthInfo): ReissueResponseDto {
        return userService.reissue(authInfo.token, authInfo.email)
    }

    @PostMapping("/v1/logout")
    fun logout(@Authenticated authInfo: AuthInfo) {
        userService.logout(authInfo.token, authInfo.email)
    }

    @PostMapping("/v1/withdraw")
    fun withdraw(@Authenticated authInfo: AuthInfo) {
        userService.withdraw(authInfo.token, authInfo.email)
    }
}
