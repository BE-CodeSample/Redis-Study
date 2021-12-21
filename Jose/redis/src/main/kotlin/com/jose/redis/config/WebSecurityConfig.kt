package com.jose.redis.config

import com.jose.redis.exception.ExceptionHandlerFilter
import com.jose.redis.jwt.JwtAuthenticationFilter
import com.jose.redis.jwt.JwtTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val stringRedisTemplate: StringRedisTemplate,
    private val exceptionHandlerFilter: ExceptionHandlerFilter
) : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity
            .httpBasic().disable()
            .csrf().disable()
            .cors().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider, stringRedisTemplate),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                exceptionHandlerFilter,
                JwtAuthenticationFilter::class.java
            )
    }

    override fun configure(web: WebSecurity?) {
        web?.ignoring()
            ?.mvcMatchers("/v1/signUp")
            ?.mvcMatchers("/v1/signIn")
    }
    // 암호화에 필요한 PasswordEncoder Bean 등록
    @Bean
    fun bCryptPasswordEncoder(): PasswordEncoder? {
        return BCryptPasswordEncoder()
    }
}
