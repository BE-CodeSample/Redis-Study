package com.jose.redis.security

import com.jose.redis.enum.RoleType
import org.springframework.core.MethodParameter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class UserInfoArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.getParameterAnnotation(Authenticated::class.java) != null &&
            parameter.parameterType == AuthInfo::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val authentication = SecurityContextHolder.getContext().authentication.principal as UserDetails
        return AuthInfo(
            webRequest.getHeader("Authorization")!!,
            authentication.username,
            mapRolesFromAuthorities(authentication.authorities)
        )
    }

    private fun mapRolesFromAuthorities(authorities: MutableCollection<out GrantedAuthority>): List<RoleType> {
        val roles: MutableList<RoleType> = mutableListOf()
        for (authority in authorities)
            roles.add(RoleType.valueOf(authority.authority))
        return roles.toList()
    }
}
