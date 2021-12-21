package com.jose.redis.entity

import com.jose.redis.enum.RoleType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Entity
class UserInfo(
    email: String,
    username: String,
    password: String,
    role: RoleType,
) : DateEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null
    @field:Email
    @field:NotBlank
    @Column(unique = true)
    var email: String = email
    @field:NotBlank
    var password: String = password
    @field:NotBlank
    var username: String = username
    var role: RoleType? = role
}
